package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

/**
 * Testing class for DynamoDB
 */
@DynamoDBTable(tableName = 'Tester')
class DynamoDBTester {

    @DynamoDBHashKey
    String theHash

    @DynamoDBRangeKey
    String theRange

    @DynamoDBIgnore
    String theIgnored

    @DynamoDBIndexHashKey(globalSecondaryIndexNames = ['foo', 'bar'], globalSecondaryIndexName = 'baz')
    String theIndexed

    @DynamoDBIndexRangeKey(
        globalSecondaryIndexNames = ['foorg', 'barrg'],
        globalSecondaryIndexName = 'bazrg',
        localSecondaryIndexNames = ['foorl', 'barrl'],
        localSecondaryIndexName = 'bazrl'
    )
    String theIndexedRange
}
