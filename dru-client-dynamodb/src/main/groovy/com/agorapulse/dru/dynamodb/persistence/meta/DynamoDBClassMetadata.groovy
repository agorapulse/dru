package com.agorapulse.dru.dynamodb.persistence.meta

import com.agorapulse.dru.dynamodb.persistence.DynamoDB
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import com.agorapulse.dru.pojo.meta.PojoClassMetadata
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.google.common.collect.ImmutableSet

/**
 * Describes DynamoDB object class.
 */
class DynamoDBClassMetadata extends PojoClassMetadata {

    private PropertyMetadata hash, range

    DynamoDBClassMetadata(Class type) {
        super(type)
    }

    @Override
    protected PropertyMetadata createPropertyMetadata(MetaProperty it) {
        return new DynamoDBPropertyMetadata(type, it.name, isPersistent(type, it.name))
    }

    protected boolean isPersistent(Class type, String name) {
        if (getAnnotation(type, name, DynamoDBIgnore)) {
            return false
        }

        return super.isPersistent(type, name)
    }

    @Override
    Object getId(Map<String, Object> fixture) {
        if (!hash && !range) {
            findHashAndRange()
        }
        Serializable hashValue = hash ? fixture[hash.name] as Serializable : null
        Serializable rangeValue = range ? fixture[range.name] as Serializable : null

        return DynamoDB.getOriginalId(hashValue, rangeValue)
    }

    @Override
    Set<String> getIdPropertyNames() {
        return [hash?.name, range?.name].grep().toSet()
    }

    PropertyMetadata getHash() {
        if (!hash) {
            findHashAndRange()
        }
        return hash
    }

    PropertyMetadata getRange() {
        if (!range) {
            findHashAndRange()
        }
        return range
    }

    Object getHash(Map<String, Object> fixture) {
        if (!hash) {
            findHashAndRange()
        }
        return hash ? fixture[hash.name] as Serializable : null
    }

    Object getRange(Map<String, Object> fixture) {
        if (!range) {
            findHashAndRange()
        }
        return range ? fixture[range.name] as Serializable : null
    }

    private void findHashAndRange() {
        for (PropertyMetadata property in persistentProperties) {
            if (getAnnotation(type, property.name, DynamoDBHashKey)) {
                hash = property
            } else if (getAnnotation(type, property.name, DynamoDBRangeKey)) {
                range = property
            }
            if (hash && range) {
                return
            }
        }
    }
}
