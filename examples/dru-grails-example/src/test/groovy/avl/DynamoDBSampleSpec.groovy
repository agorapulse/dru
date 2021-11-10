/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package avl

import com.agorapulse.dru.Dru
import com.agorapulse.dru.dynamodb.persistence.DruDynamoDBMapper
import com.agorapulse.dru.dynamodb.persistence.DynamoDB
import com.amazonaws.AmazonClientException
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import org.joda.time.DateTime
import spock.lang.AutoCleanup
import spock.lang.Specification

/**
 * Testing retrieving just single value.
 */
class DynamoDBSampleSpec extends Specification {

    // tag::plan[]
    @AutoCleanup Dru dru = Dru.create {
        from ('missionLogEntry.json') {
            map {
                to MissionLogEntry
            }
        }
    }

    void 'mission log entry has agent id assigned'() {
        given:
            String id = DynamoDB.getOriginalId(MissionLogEntry, 7, '2013-07-05T01:23:22Z')
        expect:
            dru.findByType(MissionLogEntry)
            dru.findByTypeAndOriginalId(MissionLogEntry, id)
    }
    // end::plan[]

    @SuppressWarnings('NoJavaUtilDate')
    // tag::mapper[]
    void 'use dynamodb mapper'() {
        when: 'DynamoDB mapper is created from data set'
            DynamoDBMapper mapper = DynamoDB.createMapper(dru)
            Date date = new DateTime('2013-07-05T01:23:22Z').toDate()
            Long missionId = 7

        then: 'loaded entities can be queried by this mapper'
            mapper.load(MissionLogEntry, missionId, date)
            mapper.load(new MissionLogEntry(missionId: missionId, date: date))
            mapper.query(MissionLogEntry,
                new DynamoDBQueryExpression<MissionLogEntry>().withHashKeyValues(new MissionLogEntry(missionId: missionId))
            ).size() == 2

        and: 'the can be also deleted using this mapper'
            mapper.delete(mapper.load(new MissionLogEntry(missionId: missionId, date: date)))
            mapper.query(MissionLogEntry,
                new DynamoDBQueryExpression<MissionLogEntry>().withHashKeyValues(new MissionLogEntry(missionId: missionId))
            ).size() == 1

        when: 'new entities are saved using this mapper'
            Date now = new Date()
            mapper.save(new MissionLogEntry(missionId: 7, date: now))

        then: 'they are available in the data set'
            dru.findAllByType(MissionLogEntry).find { it.missionId == 7 && it.date == now }
    }
    // end::mapper[]

    // tag::advancedMapper[]
    void 'advanced dynamodb mapper'() {
        when: 'DynamoDB mapper is created from data set'
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
            mapper.onQuery(MissionLogEntry) { MissionLogEntry entry, DynamoDBQueryExpression<MissionLogEntry> query, DynamoDBMapperConfig config ->
                return entry.agentId == 101
            }
        then:
            mapper.query(MissionLogEntry, buildComplexQuery()).size() == 2
    }
    // end::advancedMapper[]

    // tag::batchWriteFailed[]
    void 'fail some writes'() {
        when:
            DruDynamoDBMapper mapper = DynamoDB.createMapper(dru)
            mapper.onBatchWrite { Iterable<MissionLogEntry> toSave, Iterable<MissionLogEntry> toDelete ->
                [new DynamoDBMapper.FailedBatch(exception: new AmazonClientException('Failed!'))]
            }
            List<DynamoDBMapper.FailedBatch> failed = mapper.batchSave(new MissionLogEntry(missionId: 7, date: new Date()))
        then:
            failed
            failed.size() == 1
            failed[0].exception instanceof AmazonClientException
    }
    // end::batchWriteFailed[]

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
        given: 'make an offensive change but do not save'
            DynamoDBMapper mapper = DynamoDB.createMapper(dru)
            MissionLogEntryDBService service = new MissionLogEntryDBService(mapper: mapper)
            service.query(7).results.each {
                it.missionId = 123
            }
        when:
            Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.BEGINS_WITH)
                .withAttributeValueList(new AttributeValue().withS('s'))
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression<MissionLogEntry>()
                .withIndexName('typeAndAgentIdIndex')
                .withScanIndexForward(false)
                .withHashKeyValues(new MissionLogEntry(missionId: 7))
                .withConsistentRead(false)
                .withRangeKeyCondition('typeAndAgentIdIndex', rangeKeyCondition)

            PaginatedQueryList<MissionLogEntry> result = mapper.query(MissionLogEntry, queryExpression)

        then:
            result.size() == 2

        when:
            MissionLogEntry entry = result.first()

        then:
            entry.description == 'Mission succeeded by Silas Ramsbottom'
    }

    @SuppressWarnings('FactoryMethodName')
    private static DynamoDBQueryExpression<MissionLogEntry> buildComplexQuery() {
        return new DynamoDBQueryExpression<MissionLogEntry>()
    }

}
