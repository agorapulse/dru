package avl

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "Item")
class Item {

    Long listedByAgentId
    Long stolenById
    Long destroyedById
    Long returnedById

    @DynamoDBHashKey
    String name

    String description

    Set<String> tags = new LinkedHashSet<>()

    @DynamoDBIgnore
    boolean isStolen() { return stolenById != null}

    @DynamoDBIgnore
    boolean isDestroyed() { return destroyedById != null}

    @DynamoDBIgnore
    boolean isReturned() { return returnedById != null}

    @DynamoDBIgnore
    Agent getListedBy() {
        return Agent.get(listedByAgentId)
    }

    @DynamoDBIgnore
    Agent getReturnedBy() {
        return Agent.get(returnedById)
    }
    @DynamoDBIgnore
    Villain getStolenBy() {
        return Villain.get(stolenById)
    }
    @DynamoDBIgnore
    Villain getDestroyedBy() {
        return Villain.get(destroyedById)
    }
}
