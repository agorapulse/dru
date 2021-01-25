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
import spock.lang.AutoCleanup
import spock.lang.Specification

/**
 * Test loading item.
 */
class ItemSpec extends Specification {


    @AutoCleanup Dru dru = Dru.create {                                                 // <1>
        from ('item.json') {                                                            // <2>
            map { to Item }                                                             // <3>
        }
    }

    void 'entities can be access from the data set'() {
        expect:
            dru.findAllByType(Item).size() == 1                                         // <4>
        when:
            Item item = dru.findByTypeAndOriginalId(Item, ID)                           // <5>
        then:
            item
            item.name == 'PX-41'                                                        // <6>
            item.description == "The PX-41 is a very dangerous mutator engineered in the top secret PX-Labs, located in the Arctic Circle. It is capable of turning any living things in the world into a purple, furry, indestructible, mindless, killing machine that is so dangerous that it can destroy anything in its path."
            item.tags.contains('superpowers')
    }

    void 'all data are used'() {
        expect:
            dru.report.empty                                                            // <7>
    }

    private static final String ID = '050e4fcf-158d-4f44-9b8b-a6ba6809982e:PX-41'
}
