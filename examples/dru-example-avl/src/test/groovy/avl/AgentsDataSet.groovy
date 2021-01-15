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
import com.agorapulse.dru.PreparedDataSet

/**
 * Agents data set.
 */
class AgentsDataSet {
    public static final PreparedDataSet agentsMapping = Dru.prepare {                   // <1>
        any (Agent) {
            map ('manager') {
                to (Agent)
            }
            defaults {
                securityLevel = 1
            }
        }
    }

    public static final PreparedDataSet agents = Dru.prepare {                          // <2>
        include agentsMapping                                                           // <3>
        from ('agents.json') {
            map {
                to (Agent)
            }
        }
    }
}
