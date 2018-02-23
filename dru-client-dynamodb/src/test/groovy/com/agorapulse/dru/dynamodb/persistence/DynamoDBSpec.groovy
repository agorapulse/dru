package com.agorapulse.dru.dynamodb.persistence

import com.agorapulse.dru.dynamodb.persistence.meta.DynamoDBClassMetadata
import com.agorapulse.dru.persistence.meta.ClassMetadata
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

    void 'find hash index'() {
        when:
            ClassMetadata classMetadata = new DynamoDBClassMetadata(DynamoDBTester)
        then:
            classMetadata.getHashIndexProperty('foo')
            classMetadata.getHashIndexProperty('bar')
            classMetadata.getHashIndexProperty('baz')

        when:
            classMetadata.getHashIndexProperty('boo')
        then:
            thrown(IllegalArgumentException)
    }
}
