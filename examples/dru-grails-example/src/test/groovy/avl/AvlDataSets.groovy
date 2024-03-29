/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// tag::header[]
package avl

import com.agorapulse.dru.Dru
import com.agorapulse.dru.PreparedDataSet
import groovy.transform.CompileDynamic

@CompileDynamic
@SuppressWarnings(['PropertyName', 'DuplicateStringLiteral'])
class AvlDataSets {

// end::header[]

    // tag::missions[]
    static final PreparedDataSet missionMapping = Dru.prepare {
        any (Mission) {
            map ('items') {
                to (new: Item)
            }
            map ('item') {
                to (new: Item)
            }
            map ('log') {
                to(new: MissionLogEntry)
            }

            ignore 'started', 'finished'
        }

        any (MissionLogEntry) {
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
                to('villainId', Villain) { t ->
                    t.just { id }
                }
            }
            map('mission') {
                to(missionId: Mission)
            }
            map('log_type') {
                to(type: MissionLogEntryType)
            }
        }
    }

    static final PreparedDataSet agentMapping = Dru.prepare {
        any (WithSecurityLevel) {
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

        any (Person) {
            ignore 'uri'
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
    // end::missions[]

    static final PreparedDataSet missionsYaml = Dru.prepare {
        include missionMapping
        include agentMapping

        from ('missions.yml') {
            map ('missions') {
                to Mission
            }
        }
    }

    static final PreparedDataSet missionLog = Dru.prepare {
        include missionMapping
        include agentMapping

        from ('missionLogEntry.json') {
            map {
                to MissionLogEntry
            }
        }
    }

    static final PreparedDataSet missionLogWrongType = Dru.prepare {
        include missionMapping
        include agentMapping

        from ('missionLogEntryWrongType.json') {
            map {
                to MissionLogEntry
            }
        }
    }

    static final PreparedDataSet notFound = Dru.prepare {
        include missionMapping
        include agentMapping

        from ('notFound.json') {
            map {
                to MissionLogEntry
            }
        }
    }

    static final PreparedDataSet agents = Dru.prepare {
        include agentMapping

        from ('agents.json') {
            map {
                to Agent
            }
        }
    }

    static final PreparedDataSet persons = Dru.prepare {
        include agentMapping

        from ('persons.json') {
            map {
                to (Agent) {
                    when { it.type == 'agent' }
                }
                to (Villain) {
                    when { it.type == 'villain' }
                    and { !it.sercurityRank }
                }
            }
        }
    }

    static final PreparedDataSet boss = Dru.prepare {
        include agentMapping

        from ('boss.json') {
            map {
                to (Agent) {
                    map ('underling') {
                        to (staff: Agent)
                    }
                }
            }
        }
    }
// tag::footer[]

}
// end::footer[]
