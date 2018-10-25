package com.agorapulse.dru.dynamodb.persistence

import com.agorapulse.dru.dynamodb.persistence.meta.DynamoDBClassMetadata
import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import com.agorapulse.dru.pojo.meta.PojoPropertyMetadata
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
            PropertyMetadata propertyMetadata = new PojoPropertyMetadata(Date, 'update', true)
            Date a = new Date(123456788)
            Date b = new Date(123456789)
        then:
            DynamoDB.ensureUniqueString(a, propertyMetadata) != DynamoDB.ensureUniqueString(b, propertyMetadata)
    }
}
