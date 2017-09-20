package com.agorapulse.dru.dynamodb.persistence

import com.agorapulse.dru.DataSet
import com.agorapulse.dru.dynamodb.persistence.meta.DynamoDBClassMetadata
import com.agorapulse.dru.persistence.Client
import com.agorapulse.dru.persistence.ClientFactory
import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.pogo.Pogo
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

class DynamoDB extends Pogo {

    static final DynamoDB INSTANCE = new DynamoDB()

    static class Factory implements ClientFactory {
        final int index = 20000

        @Override
        boolean isSupported(Object unitTest) {
            return true
        }

        @Override
        Client newClient(Object unitTest) {
            return INSTANCE
        }
    }

    @Override
    protected boolean computeIsSupported(Class type) {
        return type.getAnnotation(DynamoDBTable)
    }

    DynamoDBClassMetadata getDynamoDBClassMetadata(Class type) {
        super.getClassMetadata(type).original as DynamoDBClassMetadata
    }

    @Override
    protected ClassMetadata createClassMetadata(Class type) {
        new DynamoDBClassMetadata(type)
    }

    static DynamoDBMapper createMapper(DataSet dataSet) {
        return new DruDynamoDBMapper(dataSet)
    }

    static Serializable getId(Object hash, Object range) {
        if (hash) {
            if (range) {
                return "${hash}:${range}"
            }
            return "${hash}:"
        }
        if (range) {
            return ":${range}"
        }
        return null
    }

    static Object getId(Object entity) {
        if (!entity) {
            return null
        }
        return INSTANCE.getClassMetadata(entity.getClass()).getId(entity.properties)
    }

    static Object getHash(Object entity) {
        if (!entity) {
            return null
        }
        return INSTANCE.getDynamoDBClassMetadata(entity.getClass()).getHash(entity.properties)
    }

    static Object getRange(Object entity) {
        if (!entity) {
            return null
        }
        return INSTANCE.getDynamoDBClassMetadata(entity.getClass()).getRange(entity.properties)
    }

}
