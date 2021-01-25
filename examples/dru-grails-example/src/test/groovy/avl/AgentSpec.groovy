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

import spock.lang.Specification

class AgentSpec extends Specification {

    void 'test is novice'() {
        expect:
            new Agent(securityLevel: 1).novice
            new Agent(securityLevel: 2).novice
            new Agent(securityLevel: 3).novice
            new Agent(securityLevel: 4).novice
            !new Agent(securityLevel: 5).novice
            !new Agent(securityLevel: 10).novice
    }

}
