package com.agorapulse.dru.dynamodb.persistence.meta

import com.agorapulse.dru.pogo.meta.PogoClassMetadata
import com.agorapulse.dru.pogo.meta.PogoPropertyMetadata
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling

class DynamoDBPropertyMetadata extends PogoPropertyMetadata {

    DynamoDBPropertyMetadata(Class type, String name, boolean persistent) {
        super(type, name, persistent)
    }

    @Override
    boolean isEmbedded() {
        return PogoClassMetadata.getAnnotation(clazz, name, DynamoDBMarshalling)
    }

}
