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
import grails.testing.mixin.integration.Integration
import org.junit.Rule
import org.springframework.test.annotation.Rollback
import spock.lang.Specification

/**
 * Test loading persons.
 */
// tag::plan[]
@Rollback
@Integration                                                                            // <1>
class GormIntegrationSpec extends Specification {

    @Rule Dru dru = Dru.plan {
        from ('agents.json') {
            map {
                to (Agent) {
                    map ('manager') {
                        to (Agent)
                    }
                }
            }
        }

        any (Agent) {
            defaults { securityLevel = 1 }
        }
    }

    void setup() {
        Agent.withNewSession { dru.load() }                                             // <2>
    }

    void 'entities can be accessed from data set and using GORM methods'() {
        expect:
            dru.findAllByType(Agent).size() == 2
            dru.findByTypeAndOriginalId(Agent, 12345).manager.name == 'Silas Ramsbottom'
        and:
            Agent.withNewSession { Agent.count() } == 2
            Agent.withNewSession { Agent.findByName('Silas Ramsbottom').id } != 12345
    }
}
// end::plan[]
