package com.agorapulse.dru.dynamodb.persistence

import com.agorapulse.dru.DataSet
import com.agorapulse.dru.DefensiveDataSetAdapter
import com.agorapulse.dru.dynamodb.persistence.meta.DynamoDBClassMetadata
import com.agorapulse.dru.persistence.Client
import com.agorapulse.dru.persistence.ClientFactory
import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.pojo.Pojo
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.fasterxml.jackson.databind.util.ISO8601Utils
import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * Client to handle DynamoDB objects.
 */
class DynamoDB extends Pojo {

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

    @Override
    String getId(Class type, Map<String, Object> properties) {
        Object hash = getDynamoDBClassMetadata(type).getHash(properties)
        Object range = getDynamoDBClassMetadata(type).getRange(properties)
        return getOriginalId(hash, range)
    }

    DynamoDBClassMetadata getDynamoDBClassMetadata(Class type) {
        super.getClassMetadata(type).original as DynamoDBClassMetadata
    }

    @Override
    protected ClassMetadata createClassMetadata(Class type) {
        new DynamoDBClassMetadata(type)
    }

    static DruDynamoDBMapper createMapper(DataSet dataSet) {
        return new DruDynamoDBMapper(DefensiveDataSetAdapter.guard(dataSet))
    }

    static Serializable getOriginalId(Object hash, Object range) {
        if (hash) {
            return range ? "${ensureUniqueString(hash)}:${ensureUniqueString(range)}" : "${ensureUniqueString(hash)}:"
        }
        return range ? ":${range}" : null
    }

    static Object getHash(Object entity) {
        if (!entity) {
            return null
        }
        return INSTANCE.getDynamoDBClassMetadata(entity.getClass()).getHash(DefaultGroovyMethods.getProperties(entity))
    }

    static Object getRange(Object entity) {
        if (!entity) {
            return null
        }
        return INSTANCE.getDynamoDBClassMetadata(entity.getClass()).getRange(DefaultGroovyMethods.getProperties(entity))
    }

    @SuppressWarnings('Instanceof')
    static Object ensureUniqueString(Object object) {
        if (object instanceof Date) {
            return ISO8601Utils.format(object)
        }
        return object
    }

}
