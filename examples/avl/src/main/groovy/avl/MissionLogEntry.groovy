package avl

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "MissionLogEntry")
class MissionLogEntry {

    private Long missionId

    @DynamoDBRangeKey
    Date date

    MissionLogEntryType type

    String description

    Long agentId
    Long villainId
    String itemName

    @DynamoDBMarshalling(marshallerClass = ExtMarshaller)
    Map<String, Object> ext

    @DynamoDBHashKey
    Long getMissionId() {
        return missionId
    }

    void setMissionId(Long missionId) {
        this.missionId = missionId
    }
}
