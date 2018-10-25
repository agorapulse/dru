package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = 'EntityWithCustomIds')
class EntityWithCustomIds {

    private EntityWithCustomIdsId parentId
    private EntityWithCustomIdsId id

    EntityWithCustomIds() { }

    EntityWithCustomIds(EntityWithCustomIdsId parentId, EntityWithCustomIdsId id) {
        this.parentId = parentId
        this.id = id
    }

    @DynamoDBHashKey
    EntityWithCustomIdsId getParentId() { return parentId }
    void setParentId(EntityWithCustomIdsId parentId) { this.parentId = parentId }


    @DynamoDBRangeKey
    EntityWithCustomIdsId getId() { return id }
    void setId(EntityWithCustomIdsId id) { this.id = id }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        EntityWithCustomIds that = (EntityWithCustomIds) o

        if (id != that.id) return false
        if (parentId != that.parentId) return false

        return true
    }

    int hashCode() {
        int result
        result = (parentId != null ? parentId.hashCode() : 0)
        result = 31 * result + (id != null ? id.hashCode() : 0)
        return result
    }
}

class EntityWithCustomIdsId implements Serializable {
    String value

    EntityWithCustomIdsId() {
        // for jackson
    }

    EntityWithCustomIdsId(String value) {
        this.value = value
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        EntityWithCustomIdsId that = (EntityWithCustomIdsId) o

        if (value != that.value) return false

        return true
    }

    int hashCode() {
        return (value != null ? value.hashCode() : 0)
    }
}
