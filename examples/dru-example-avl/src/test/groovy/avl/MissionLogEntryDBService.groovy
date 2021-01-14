package avl

import grails.plugin.awssdk.dynamodb.AbstractDBService

class MissionLogEntryDBService extends AbstractDBService<MissionLogEntry> {

    MissionLogEntryDBService() {
        super(MissionLogEntry)
    }

}
