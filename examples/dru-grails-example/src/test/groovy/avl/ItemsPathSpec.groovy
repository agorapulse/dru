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
 * Testing importing items from property.
 */
@SuppressWarnings('LineLength')
class ItemsPathSpec extends Specification {

    private static final String ID = '050e4fcf-158d-4f44-9b8b-a6ba6809982e:PX-41'

    // tag::plan[]
    @AutoCleanup Dru dru = Dru.create {
        from ('items.json') {
            map ('mission.items') {
                to Item
            }
        }
    }
    // end::plan[]

    void 'entities can be access from the data set'() {
        expect:
            dru.findAllByType(Item).size() == 1
        when:
            Item item = dru.findByTypeAndOriginalId(Item, ID)
        then:
            item
            item.name == 'PX-41'
            item.description == 'The PX-41 is a very dangerous mutator engineered in the top secret PX-Labs, located in the Arctic Circle. It is capable of turning any living things in the world into a purple, furry, indestructible, mindless, killing machine that is so dangerous that it can destroy anything in its path.'
            item.tags.contains('superpowers')
    }

    void 'all data are used'() {
        expect:
            dru.report.empty
    }

}
