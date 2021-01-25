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
package com.agorapulse.dru.dynamodb.persistence

import com.agorapulse.dru.Dru
import com.agorapulse.dru.dynamodb.persistence.meta.DynamoDBClassMetadata
import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import com.agorapulse.dru.pojo.meta.PojoPropertyMetadata
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test for DynamoDB client.
 */
class DynamoDBSpec extends Specification {

    void 'nulls returns nulls'() {
        expect:
            DynamoDB.getHash(null) == null
            DynamoDB.getRange(null) == null
    }

    void 'find hash'() {
        when:
            ClassMetadata classMetadata = new DynamoDBClassMetadata(DynamoDBTester)
        then:
            classMetadata.hash.name == 'theHash'
    }

    void 'find hash from fixture'() {
        when:
            ClassMetadata classMetadata = new DynamoDBClassMetadata(DynamoDBTester)
        then:
            classMetadata.getHash(theHash: 'someHash', theRange: 'someRange') == 'someHash'
    }

    void 'find range'() {
        when:
            ClassMetadata classMetadata = new DynamoDBClassMetadata(DynamoDBTester)
        then:
            classMetadata.range.name == 'theRange'
    }

    void 'find range from fixture'() {
        when:
            ClassMetadata classMetadata = new DynamoDBClassMetadata(DynamoDBTester)
        then:
            classMetadata.getRange(theHash: 'someHash', theRange: 'someRange') == 'someRange'
    }

    void 'ignored are not persistent'() {
        when:
            ClassMetadata classMetadata = new DynamoDBClassMetadata(DynamoDBTester)
        then:
            !classMetadata.getPersistentProperty('theIgnored').persistent
    }

    @Unroll
    void 'id is #id for #fixture'() {
        when:
            ClassMetadata classMetadata = new DynamoDBClassMetadata(DynamoDBTester)
        then:
            classMetadata.getId(fixture) == id
        where:
            id          | fixture
            'foo:bar'   | [theHash: 'foo', theRange: 'bar']
            'foo:'      | [theHash: 'foo']
            ':bar'      | [theRange: 'bar']
            null        | [:]
    }

    @SuppressWarnings('UnnecessaryObjectReferences')
    void 'find hash index'() {
        when:
            ClassMetadata classMetadata = new DynamoDBClassMetadata(DynamoDBTester)
        then:
            classMetadata.getHashIndexProperty('foo')
            classMetadata.getHashIndexProperty('bar')
            classMetadata.getHashIndexProperty('baz')
            !classMetadata.getHashIndexProperty('boo')
        and:
            classMetadata.getRangeIndexProperty('foorg')
            classMetadata.getRangeIndexProperty('barrg')
            classMetadata.getRangeIndexProperty('bazrg')
            classMetadata.getRangeIndexProperty('foorl')
            classMetadata.getRangeIndexProperty('barrl')
            classMetadata.getRangeIndexProperty('bazrl')
            !classMetadata.getRangeIndexProperty('boo')
    }

    void 'check timestamp ids are different'() {
        when:
            PropertyMetadata propertyMetadata = new PojoPropertyMetadata(WithDate, 'update', true)
            Date a = new Date(123456788)
        then:
            DynamoDB.ensureUniqueString(a, propertyMetadata) !=
                DynamoDB.ensureUniqueString(123456789, propertyMetadata)

            DynamoDB.ensureUniqueString(a, propertyMetadata) !=
                DynamoDB.ensureUniqueString(new ISO8601DateFormat().format(new Date(123456789)), propertyMetadata)

        and: 'using getOriginalId without type is deprecated'
            DynamoDB.getOriginalId(a, null) != '123456788:'
    }

    void 'items which cannot be simply fetched'() {
        when:
            EntityWithCustomIds entity = new EntityWithCustomIds(new EntityWithCustomIdsId('Foo'), new EntityWithCustomIdsId('Bar'))
            EntityWithCustomIds other = new EntityWithCustomIds(new EntityWithCustomIdsId('Bar'), new EntityWithCustomIdsId('Foo'))

            DruDynamoDBMapper mapper = DynamoDB.createMapper(Dru.create(this))
            mapper.save(entity)
        then:
            mapper.load(entity)
            mapper.load(EntityWithCustomIds, entity.parentId, entity.id)
            !mapper.load(other)
            !mapper.load(EntityWithCustomIds, other.parentId, other.id)
    }
}

class WithDate {
    Date update
}
