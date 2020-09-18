package avl

import grails.plugin.awssdk.dynamodb.AbstractDBService

/**
 * Item DynamoDB service.
 */
class ItemService extends AbstractDBService<Item> {

    ItemService() {
        super(Item)
    }

}
