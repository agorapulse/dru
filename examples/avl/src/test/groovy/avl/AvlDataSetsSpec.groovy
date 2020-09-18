package avl

import com.agorapulse.dru.Dru
import com.agorapulse.dru.dynamodb.persistence.DruDynamoDBMapper
import com.agorapulse.dru.dynamodb.persistence.DynamoDB
import com.agorapulse.dru.gorm.persistence.Gorm
import com.agorapulse.dru.persistence.Client
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedParallelScanList
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Tests for AvlDataSet.
 */
@SuppressWarnings('Println')
class AvlDataSetsSpec extends Specification implements DataTest {

    @Rule Dru dru = Dru.plan {
        whenLoaded {
            println it.report
        }
    }

    void ' warmup'() { expect: true }

    void 'loading two data sets with same content'() {
        given:
            dru.load(AvlDataSets.missions, AvlDataSets.agents)
        expect:
            dru.report.empty
            dru.findAllByType(Mission).size() == 2
            dru.findByTypeAndOriginalId(Mission, 7)
            dru.findAllByType(Agent).size() == 3
            dru.findAllByType(Assignment).size() == 4
            dru.findAllByType(Villain).size() == 2
            dru.findAllByType(Item).size() == 2
    }

    void 'entities can be access from the data set - yaml'() {
        given:
            dru.load(AvlDataSets.missionsYaml)
        expect:
            dru.report.empty
            dru.findAllByType(Mission).size() == 2
            dru.findByTypeAndOriginalId(Mission, 7)
            dru.findAllByType(Agent).size() == 3
            dru.findAllByType(Assignment).size() == 4
            dru.findAllByType(Villain).size() == 2
            dru.findAllByType(Item).size() == 2
    }

    void 'GORM entities are persisted'() {
        given:
            dru.load(AvlDataSets.missions)
        expect:
            Mission.list().size() == 2
            Agent.list().size() == 3
            Assignment.list().size() == 4
            Villain.list().size() == 2
    }

    void 'DynamoDB mapper can access loaded entities'() {
        given:
            dru.load(AvlDataSets.missions)
        when:
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
        then:
            mapper.count(Item, new DynamoDBQueryExpression<Item>()) == 2
            mapper.count(MissionLogEntry, new DynamoDBScanExpression()) == 7
            mapper.load(new Item(id: 'e30d0de3-2415-42b2-aa31-dceba3dcc3fa', name: 'Dupont Diamond'))
            mapper.load(Item, 'e30d0de3-2415-42b2-aa31-dceba3dcc3fa', 'Dupont Diamond')
        when:
            Map<String, List<Object>> batchFetch = mapper.batchLoad([new Item(id: 'e30d0de3-2415-42b2-aa31-dceba3dcc3fa', name: 'Dupont Diamond')])
        then:
            batchFetch.size() == 1
            batchFetch['Item']
            batchFetch['Item'].size() == 1
            batchFetch['Item'][0].name == 'Dupont Diamond'
    }

    void 'DynamoDB mapper can query the loaded entities'() {
        given:
            dru.load(AvlDataSets.missions)
        when:
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
            MissionLogEntry hashKey = new MissionLogEntry(missionId: dru.findByTypeAndOriginalId(Mission, 7).id)
            DynamoDBQueryExpression hashQuery = new DynamoDBQueryExpression<MissionLogEntry>().withHashKeyValues(hashKey)
            QueryResultPage<MissionLogEntry> hashPage = mapper.queryPage(MissionLogEntry, hashQuery)
        then:
            hashPage.count == 7
            Condition rangeCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.BETWEEN)
                .withAttributeValueList(
                new AttributeValue().withS('2013-07-05T00:00:00Z'),
                new AttributeValue().withS('2013-07-10T00:00:00Z')
            )
        when:
            DynamoDBQueryExpression rangeQuery = new DynamoDBQueryExpression<MissionLogEntry>()
                .withHashKeyValues(hashKey)
                .withRangeKeyCondition('date', rangeCondition)
                .withLimit(2)
            QueryResultPage<MissionLogEntry> rangePage = mapper.queryPage(MissionLogEntry, rangeQuery)
        then:
            rangePage.count == 2
        when:
            QueryResultPage<MissionLogEntry> nextRangePage = mapper.queryPage(MissionLogEntry, rangeQuery.withExclusiveStartKey(rangePage.lastEvaluatedKey))
        then:
            nextRangePage.count == 1
        when:
            PaginatedQueryList<MissionLogEntry> paginatedRangeQuery = mapper.query(MissionLogEntry, rangeQuery)
        then:
            paginatedRangeQuery.size() == 3
            paginatedRangeQuery.atEndOfResults()
            !paginatedRangeQuery.fetchNextPage()
    }

    void 'DynamoDB mapper can scan the loaded entities'() {
        given:
            dru.load(AvlDataSets.missions)
        when:
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
            Condition scanFilterCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(
                new AttributeValue().withN(Agent.findByName('Felonius Gru').id.toString())
            )
            DynamoDBScanExpression hashScanExpression = new DynamoDBScanExpression().withScanFilter(agentId: scanFilterCondition).withLimit(2)
            ScanResultPage<MissionLogEntry> scanPage = mapper.scanPage(MissionLogEntry, hashScanExpression)
        then:
            scanPage.count == 2
        when:
            ScanResultPage<MissionLogEntry> nextScanPage = mapper.scanPage(MissionLogEntry, hashScanExpression.withExclusiveStartKey(scanPage.lastEvaluatedKey))
        then:
            nextScanPage.count == 1
        when:
            ScanResultPage<MissionLogEntry> emptyScanPage = mapper.scanPage(MissionLogEntry, hashScanExpression.withExclusiveStartKey(
                missionId: new AttributeValue().withN(Mission.findByTitle('Save the World from PX-41').id.toString()),
                date: new AttributeValue().withS('2013-07-23T19:13:20.000Z')
            ))
        then:
            emptyScanPage.count == 0
        when:
            PaginatedScanList<MissionLogEntry> scan = mapper.scan(MissionLogEntry, hashScanExpression)
        then:
            scan.size() == 3
            scan.atEndOfResults()
            !scan.fetchNextPage()
        when:
            PaginatedParallelScanList<MissionLogEntry> scanParallelPage = mapper.parallelScan(MissionLogEntry, hashScanExpression, 5)
        then:
            scanParallelPage.size() == 3
            scanParallelPage.atEndOfResults()
            !scanParallelPage.fetchNextPage()
    }

    void 'DynamoDB mapper updates data set'() {
        given:
            dru.load(AvlDataSets.missions)
        when:
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
            Item moon = new Item(name: 'Moon')
            mapper.save(moon)
        then:
            mapper.count(Item, new DynamoDBQueryExpression<Item>()) == 3
            dru.findAllByType(Item).size() == 3
        when:
            mapper.batchWrite([
                new Item(name: 'Lollipop'),
                new Item(name: 'Statue of Liberty (from Las Vegas)'),
            ], [
                new Item(name: 'Moon'),
                new Item(name: 'Phoebe'),
            ])
        then:
            mapper.count(Item, new DynamoDBQueryExpression<Item>()) == 4
    }

    void 'Complex DynamoDB scan can be done using onScan'() {
        given:
            dru.load(AvlDataSets.missions)
        when:
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
            Long gruId = Agent.findByName('Felonius Gru').id
            mapper.onScan(MissionLogEntry) {
                it.agentId == gruId
            }
            PaginatedScanList<MissionLogEntry> scan = mapper.scan(MissionLogEntry, new DynamoDBScanExpression().withIndexName('agentId'))
        then:
            scan.size() == 3
    }

    void 'Complex DynamoDB query can be done using onScan'() {
        given:
            dru.load(AvlDataSets.missions)
        when:
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
            Long gruId = Agent.findByName('Felonius Gru').id
            mapper.onQuery(MissionLogEntry) { MissionLogEntry it, DynamoDBQueryExpression expression ->
                it[expression.indexName] == gruId
            }
            mapper.onQuery(MissionLogEntry) { MissionLogEntry it, DynamoDBQueryExpression expression, DynamoDBMapperConfig config ->
                // testing closure with full parameters
                true
            }
            PaginatedQueryList<MissionLogEntry> query = mapper.query(MissionLogEntry, new DynamoDBQueryExpression<MissionLogEntry>().withIndexName('agentId'))
        then:
            query.size() == 3
    }

    void 'usage with grails dynamodb plugin'() {
        given:
            String id = 'f9e716df-2d73-42cb-9cf5-9334617f73c1'
            String name = 'Something'
            dru.load(AvlDataSets.missions)
        when:
            ItemService itemService = new ItemService(mapper: DynamoDB.createMapper(dru))
            itemService.save(new Item(id: id, name: name))
        then:
            dru.findByTypeAndOriginalId(Item, DynamoDB.getOriginalId(Item, id, name))
    }

    void 'load agents'() {
        when:
            dru.load {
                include AvlDataSets.agents
                // also test duplicate load
                include AvlDataSets.agents
            }
        then:
            noExceptionThrown()
            dru.report.empty
            Agent.list().size() == 3
            Mission.list().size() == 2
            Assignment.list().size() == 4
        and:
            Agent.findByNameAndSecurityLevel('Felonius Gru', 2)
            Agent.findByNameAndSecurityLevel('Lucy Wilde', 5)
    }

    void 'load persons'() {
        when:
            dru.load {
                include AvlDataSets.persons
            }
        then:
            noExceptionThrown()
            dru.report.empty
            Agent.list().size() == 2
            Mission.list().size() == 2
            Assignment.list().size() == 4
            Villain.list().size() == 1
        and:
            Agent.findByNameAndSecurityLevel('Felonius Gru', 2)
            Agent.findByNameAndSecurityLevel('Lucy Wilde', 5)
            Villain.findByName('El Macho')
    }

    void 'load boss'() {
        when:
            dru.load(AvlDataSets.boss)
        then:
            noExceptionThrown()
            Agent.list().size() == 2
            Agent.findByName('Silas Ramsbottom').staff.size() == 1
            Agent.findByName('Silas Ramsbottom').staff.contains(Agent.findByName('Lucy Wilde'))
    }

    void 'load mission log'() {
        when:
            dru.load(AvlDataSets.missionLog)
        then:
            thrown(IllegalStateException)
    }

    void 'load source which does not exist'() {
        when:
            dru.load(AvlDataSets.notFound)
        then:
            thrown(IllegalStateException)
    }

    void 'load mission log with wrong type used'() {
        when:
            dru.load(AvlDataSets.missionLogWrongType)
        then:
            thrown(IllegalArgumentException)
    }

    void 'test add to'() {
        when:
            Client gorm = new Gorm.Factory().newClient(this)
            dru.load(AvlDataSets.agents)
            Agent lucy = Agent.findByName('Lucy Wilde')
            Agent gru = Agent.findByName('Felonius Gru')
        then:
            lucy
            gru
            !lucy.staff?.contains(gru)
        when:
            gorm.addTo(lucy, 'staff', gru)
        then:
            lucy.staff.contains(gru)

    }

}
