package avl

import grails.plugin.awssdk.dynamodb.AbstractDBService

/**
 * MissionLogEntry DynamoDB service.
 */
class MissionLogEntryDBService extends AbstractDBService<MissionLogEntry> {

    MissionLogEntryDBService() {
        super(MissionLogEntry)
    }

}
