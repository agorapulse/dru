package avl

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "Item")
class Item {

    @DynamoDBHashKey
    String name

    String description

    Set<String> tags = new LinkedHashSet<>()
}
