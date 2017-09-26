package avl

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
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

    Long agentId
    Long villainId
    String itemName

// tag::footer[]
}
// end::footer[]
