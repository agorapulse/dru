package avl

import com.agorapulse.dru.Dru
import com.agorapulse.dru.dynamodb.persistence.DruDynamoDBMapper
import com.agorapulse.dru.dynamodb.persistence.DynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import org.joda.time.DateTime
import org.junit.Rule
import spock.lang.IgnoreRest
import spock.lang.Specification

/**
 * Testing retrieving just single value.
 */
class DynamoDBSampleSpec extends Specification {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('missionLogEntry.json') {
            map {
                to MissionLogEntry
            }
        }
    }

    void 'mission log entry has agent id assigned'() {
        given:
            String id = DynamoDB.getOriginalId(7, '2013-07-05T01:23:22Z')
        expect:
            dru.findByType(MissionLogEntry)
            dru.findByTypeAndOriginalId(MissionLogEntry, id)
    }
    // end::plan[]

    // tag::mapper[]
    void 'use dynamodb mapper'() {
        when: "DynamoDB mapper is created from data set"
            DynamoDBMapper mapper = DynamoDB.createMapper(dru)
            Date date = new DateTime('2013-07-05T01:23:22Z').toDate()
            Long missionId = 7

        then: "loaded entities can be queried by this mapper"
            mapper.load(MissionLogEntry, missionId, date)
            mapper.load(new MissionLogEntry(missionId: missionId, date: date))
            mapper.query(MissionLogEntry,
                new DynamoDBQueryExpression<MissionLogEntry>().withHashKeyValues(new MissionLogEntry(missionId: missionId))
            ).size() == 2

        and: "the can be also deleted using this mapper"
            mapper.delete(mapper.load(new MissionLogEntry(missionId: missionId, date: date)))
            mapper.query(MissionLogEntry,
                new DynamoDBQueryExpression<MissionLogEntry>().withHashKeyValues(new MissionLogEntry(missionId: missionId))
            ).size() == 1

        when: "new entities are saved using this mapper"
            Date now = new Date()
            mapper.save(new MissionLogEntry(missionId: 7, date: now))

        then: "they are available in the data set"
            dru.findAllByType(MissionLogEntry).find { it.missionId == 7 && it.date == now}

    }
    // end::mapper[]

    // tag::advancedMapper[]
    void 'advanced dynamodb mapper'() {
        when: "DynamoDB mapper is created from data set"
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
            mapper.onQuery(MissionLogEntry) { MissionLogEntry entry, DynamoDBQueryExpression<MissionLogEntry> query, DynamoDBMapperConfig config ->
                return entry.agentId == 101
            }
        then:
            mapper.query(MissionLogEntry, buildCompexQuery()).size() == 2

    }
    // end::advancedMapper[]

    private static DynamoDBQueryExpression<MissionLogEntry> buildCompexQuery() {
        return new DynamoDBQueryExpression<MissionLogEntry>()
    }

    // tag::grailsService[]
    void 'use grails service'() {
        when:
            MissionLogEntryDBService service = new MissionLogEntryDBService()
            service.mapper = DynamoDB.createMapper(dru)
        then:
            service.query(7).count == 2
    }
    // end::grailsService[]

    void 'use secondary global index'() {
        when:
            DynamoDBMapper mapper = DynamoDB.createMapper(dru)

            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression<MissionLogEntry>()
                .withHashKeyValues(new MissionLogEntry(agentId: 101))
                .withIndexName('agentIdMissionLogEntryIndex')
                .withConsistentRead(false)

            PaginatedQueryList<MissionLogEntry> result = mapper.query(MissionLogEntry, queryExpression)

        then:
            result.size() == 2

        when:
            MissionLogEntry entry = result.first()

        then:
            entry.description == 'Mission started by Silas Ramsbottom'
    }

    void 'use local range key index'() {
        when:
            Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.BEGINS_WITH)
                .withAttributeValueList(new AttributeValue().withS('s'))

            DynamoDBMapper mapper = DynamoDB.createMapper(dru)

            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression<MissionLogEntry>()
                .withIndexName('typeAndAgentIdIndex')
                .withScanIndexForward(false)
                .withHashKeyValues(new MissionLogEntry(missionId: 7))
                .withConsistentRead(false)
                .withRangeKeyCondition("typeAndAgentIdIndex", rangeKeyCondition)

            PaginatedQueryList<MissionLogEntry> result = mapper.query(MissionLogEntry, queryExpression)

        then:
            result.size() == 2

        when:
            MissionLogEntry entry = result.first()

        then:
            entry.description == 'Mission succeeded by Silas Ramsbottom'
    }

}
