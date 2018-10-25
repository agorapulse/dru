package com.agorapulse.dru.dynamodb.persistence

import com.agorapulse.dru.DataSet
import com.agorapulse.dru.DataSetGuardian
import com.agorapulse.dru.dynamodb.persistence.meta.DynamoDBClassMetadata
import com.agorapulse.dru.persistence.Client
import com.agorapulse.dru.persistence.ClientFactory
import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import com.agorapulse.dru.pojo.Pojo
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * Client to handle DynamoDB objects.
 */
class DynamoDB extends Pojo {

    private static final ISO8601DateFormat ISO_DATE_FORMAT = new ISO8601DateFormat()

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
        DynamoDBClassMetadata classMetadata = getDynamoDBClassMetadata(type)
        Object hash = classMetadata.getHash(properties)
        Object range = classMetadata.getRange(properties)
        return getOriginalId(hash, classMetadata.hash, range, classMetadata.range)
    }

    DynamoDBClassMetadata getDynamoDBClassMetadata(Class type) {
        super.getClassMetadata(type).original as DynamoDBClassMetadata
    }

    @Override
    protected ClassMetadata createClassMetadata(Class type) {
        new DynamoDBClassMetadata(type)
    }

    static DruDynamoDBMapper createMapper(DataSet dataSet) {
        return new DruDynamoDBMapper(DataSetGuardian.guard(dataSet))
    }

    /**
     * @param hash hash value
     * @param range range value
     * @return original id for given hash and range
     * @deprecated use implementation with type or property metadata instead
     * @see #getOriginalId(Object,PropertyMetadata,Object,PropertyMetadata)
     * @see #getOriginalId(Class,PropertyMetadata,PropertyMetadata)
     */
    @Deprecated
    static Serializable getOriginalId(Object hash, Object range) {
        return getOriginalId(hash, null, range, null)
    }

    static Serializable getOriginalId(Class type, Object hash, Object range) {
        DynamoDBClassMetadata classMetadata = DynamoDB.INSTANCE.getDynamoDBClassMetadata(type)
        return getOriginalId(hash, classMetadata.hash, range, classMetadata.range)
    }

    @SuppressWarnings('IfStatementCouldBeTernary')
    static Serializable getOriginalId(Object hash, PropertyMetadata hashMetadata, Object range, PropertyMetadata rangeMetadata) {
        if (hash) {
            if (range) {
                return "${ensureUniqueString(hash, hashMetadata)}:${ensureUniqueString(range, rangeMetadata)}"
            }
            return "${ensureUniqueString(hash, hashMetadata)}:"
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
    static Object ensureUniqueString(Object object, PropertyMetadata propertyMetadata) {
        if (object && propertyMetadata && Date.isAssignableFrom(propertyMetadata.type)) {
            if (object instanceof Date) {
                return String.valueOf(object.time)
            }
            if (object instanceof Number) {
                return String.valueOf(object.toLong())
            }
            return String.valueOf(ISO_DATE_FORMAT.parse(object.toString()).time)
        }
        return object
    }

}
