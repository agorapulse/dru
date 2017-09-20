package avl

import grails.plugin.awssdk.dynamodb.AbstractDBService

class ItemService extends AbstractDBService<Item> {

    ItemService() {
        super(Item)
    }

}
