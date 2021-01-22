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
package avl

import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import spock.lang.AutoCleanup
import spock.lang.Specification

/**
 * Testing retrieving just single value.
 */
class JustSpec extends Specification implements DataTest {

    // tag::plan[]
    @AutoCleanup Dru dru = Dru.create {
        from ('missionLogEntry.json') {
            map {
                to (MissionLogEntry) {
                    map ('agent') {
                        to (agentId: Agent) {
                            just { id }
                            defaults {
                                securityLevel = 1
                            }
                        }
                    }
                }
            }
        }
    }

    void 'mission log entry has agent id assigned'() {
        expect:
            dru.findByType(Agent)
            dru.findByType(MissionLogEntry).agentId == 1
    }
    // end::plan[]
}
