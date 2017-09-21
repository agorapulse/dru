package avl

import com.agorapulse.dru.Dru
import com.agorapulse.dru.PreparedDataSet

class AvlDataSets {

    static final PreparedDataSet missionMapping = Dru.prepare {
        any (Mission) {
            map ('items') {
                to (new: Item)
            }
            map ('log') {
                to(new: MissionLogEntry) {
                    map('item') {
                        to(itemName: Item) {
                            just { name }
                        }
                    }
                    map('agent') {
                        to(agentId: Agent) {
                            just { id }
                        }
                    }
                    map('villain') {
                        to(villainId: Villain) {
                            just { id }
                        }
                    }
                    map('mission') {
                        to(missionId: Mission) {
                            just { id }
                        }
                    }
                }
            }

            ignore 'started', 'finished'
        }
    }

    static final PreparedDataSet missions = Dru.prepare {
        include missionMapping

        from ('missions.json') {
            map ('missions') {
                to Mission
            }
        }
    }

}
