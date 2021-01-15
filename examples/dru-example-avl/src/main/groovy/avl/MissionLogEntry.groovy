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
package avl

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

// tag::header[]
@DynamoDBTable(tableName = "MissionLogEntry")
class MissionLogEntry {
// end::header[]

    private
    // tag::hashKey[]
    Long missionId
    // end::hashKey[]

    // tag::hashKeyAnno[]
    @DynamoDBHashKey
    // end::hashKeyAnno[]
    Long getMissionId() {
        return missionId
    }

    void setMissionId(Long missionId) {
        this.missionId = missionId
    }

    // tag::properties[]
    @DynamoDBRangeKey
    Date date

    MissionLogEntryType type

    String description
    // end::properties[]

    // tag::ext[]
    @DynamoDBMarshalling(marshallerClass = ExtMarshaller)
    Map<String, Object> ext
    // end::ext[]

    @DynamoDBIndexHashKey(globalSecondaryIndexName='agentIdMissionLogEntryIndex')
    @DynamoDBAttribute
    Long agentId

    @DynamoDBIndexRangeKey(localSecondaryIndexName = 'typeAndAgentIdIndex')
    String getTypeAndAgentId() {
        "${type}_${agentId}"
    }

    Long villainId
    String itemName

    Map properties = null

// tag::footer[]
}
// end::footer[]
