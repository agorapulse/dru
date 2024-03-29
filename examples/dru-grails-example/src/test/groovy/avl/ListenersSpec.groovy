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
 * Test loading persons.
 */
@SuppressWarnings('NestedBlockDepth')
class ListenersSpec extends Specification implements DataTest {

    @SuppressWarnings(['unused', 'UnusedPrivateField'])
    private static final List<Map<String, Object>> AGENTS = [
        [
            id           : 12345,
            name         : 'Felonius Gru',
            bio          : 'Born from the family with long line of villainy and formerly the world\'s greatest villain.',
            securityLevel: 2,
            manager      : [
                id  : 101,
                name: 'Silas Ramsbottom'
            ]
        ]
    ]

    // tag::plan[]
    @AutoCleanup Dru dru = Dru.create {
        from ('AGENTS') {
            map {
                to (Agent) {
                    map ('manager') {
                        to (Agent) {
                            defaults { securityLevel = 1 }
                        }
                    }
                }
            }
        }
    }

    void 'calling when loaded hook'() {
        when:
            int count = 0
            dru.load {
                whenLoaded {
                    count++
                }
            }
        then:
            count == 1                                                                  // <1>
        when:
            dru.loaded()
        then:
            count == 2                                                                  // <2>
    }
    // end::plan[]

    void 'calling on change hook'() {
        when:
            int count = 0
            dru.load {
                onChange {
                    count++
                }
            }
        then:
            count == 0

        when:
            dru.changed()
        then:
            count == 1

        when:
            Agent gru = new Agent(id: 23456, name: 'Gru')
            dru.add(gru)

        then:
            count == 2

        when:
            dru.remove(gru)
        then:
            count == 3
    }

}
