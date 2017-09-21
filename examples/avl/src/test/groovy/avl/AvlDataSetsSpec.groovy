package avl

import com.agorapulse.dru.Dru
import com.agorapulse.dru.dynamodb.persistence.DruDynamoDBMapper
import com.agorapulse.dru.dynamodb.persistence.DynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

class AvlDataSetsSpec extends Specification implements DataTest {

    @Rule Dru dru = Dru.steal(this)

    void 'load assignments'() {
        when:
            dru.load(AvlDataSets.missions)
            println dru.report
        then:
            dru.findAllByType(Mission).size() == 2
            dru.findByTypeAndOriginalId(Mission, 7)
            dru.findAllByType(Agent).size() == 3
            dru.findAllByType(Assignment).size() == 4
            dru.findAllByType(Villain).size() == 2
            dru.findAllByType(Item).size() == 2
        and:
            Mission.list().size() == 2
            Agent.list().size() == 3
            Assignment.list().size() == 4
            Villain.list().size() == 2
        when:
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
        then:
            mapper.count(Item.class, new DynamoDBQueryExpression<Item>()) == 2
            mapper.load(new Item(name: "Dupont Diamond"))
            mapper.load(Item.class, "Dupont Diamond")
        when:
            Map<String, List<Object>> batchFetch = mapper.batchLoad([new Item(name: "Dupont Diamond")])
        then:
            batchFetch.size() == 1
            batchFetch['Item']
            batchFetch['Item'].size() == 1
            batchFetch['Item'][0].name == "Dupont Diamond"
        when:
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
            QueryResultPage<MissionLogEntry> rangePage = mapper.queryPage(MissionLogEntry, rangeQuery)
        then:
            rangePage.count == 3
        when:
            Condition scanFilterCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(
                    new AttributeValue().withN(Agent.findByName('Felonius Gru').id.toString())
                )
            DynamoDBScanExpression hashScanExpression = new DynamoDBScanExpression().withScanFilter(agentId: scanFilterCondition)
            PaginatedScanList<MissionLogEntry> scanPage = mapper.scan(MissionLogEntry, hashScanExpression)
        then:
            scanPage.size() == 3
        when:
            Item moon = new Item(name: 'Moon')
            mapper.save(moon)
        then:
            mapper.count(Item, new DynamoDBQueryExpression<Item>()) == 3
            dru.findAllByType(Item).size() == 3
        when:
            mapper.batchWrite([new Item(name: 'Lollipop'), new Item(name: 'Statue of Liberty (from Las Vegas)')], [new Item(name: 'Moon')])
        then:
            mapper.count(Item, new DynamoDBQueryExpression<Item>()) == 4
    }

}
