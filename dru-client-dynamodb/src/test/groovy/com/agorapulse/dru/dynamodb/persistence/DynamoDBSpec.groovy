package com.agorapulse.dru.dynamodb.persistence

import spock.lang.Specification

/**
 * Test for DynamoDB client.
 */
class DynamoDBSpec extends Specification {

    void 'nulls returns nulls'() {
        expect:
            DynamoDB.getId(null) == null
            DynamoDB.getHash(null) == null
            DynamoDB.getRange(null) == null
    }

}
