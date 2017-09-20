package com.agorapulse.dru.dynamodb.persistence.meta

import com.agorapulse.dru.pojo.meta.PojoClassMetadata
import com.agorapulse.dru.pojo.meta.PojoPropertyMetadata
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling

/**
 * Describes DynamoDB object property.
 */
class DynamoDBPropertyMetadata extends PojoPropertyMetadata {

    DynamoDBPropertyMetadata(Class type, String name, boolean persistent) {
        super(type, name, persistent)
    }

    @Override
    boolean isEmbedded() {
        return PojoClassMetadata.getAnnotation(clazz, name, DynamoDBMarshalling)
    }

}
