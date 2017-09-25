package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
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
}
