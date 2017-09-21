package avl

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "MissionLogEntry")
class MissionLogEntry {

    @DynamoDBHashKey
    Long missionId

    @DynamoDBRangeKey
    Date date

    MissionLogEntryType type

    String description

    Long agentId
    Long villainId
    String itemName

}
