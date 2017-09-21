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

    static final PreparedDataSet agentMapping = Dru.prepare {
        any (Agent) {
            overrides {
                if (it.rank) {
                    securityLevel = it.rank
                }
            }
            defaults {
                securityLevel = 1
            }
            ignore {
                novice
            }
        }
    }

    static final PreparedDataSet missions = Dru.prepare {
        include missionMapping
        include agentMapping

        from ('missions.json') {
            map ('missions') {
                to Mission
            }
        }
    }

    static final PreparedDataSet agents = Dru.prepare {
        include agentMapping

        from ('agents.json') {
            map ('agents') {
                to Agent
            }
        }
    }

}
