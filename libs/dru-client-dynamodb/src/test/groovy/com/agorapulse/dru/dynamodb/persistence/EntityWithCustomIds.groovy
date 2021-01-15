/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

/**
 * Sample DynamoDB entity with custom ids.
 */
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

    @SuppressWarnings(['IfStatementBraces', 'UnnecessaryIfStatement', 'IfStatementCouldBeTernary'])
    boolean equals(Object o) {
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

    private static final long serialVersionUID = 1

    String value

    EntityWithCustomIdsId() {
        // for jackson
    }

    EntityWithCustomIdsId(String value) {
        this.value = value
    }

    @SuppressWarnings(['IfStatementBraces', 'UnnecessaryIfStatement', 'IfStatementCouldBeTernary'])
    boolean equals(Object o) {
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
