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
package com.agorapulse.dru

import avl.Item
import groovy.json.JsonOutput
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileSourceSpec extends Specification {

    @Rule TemporaryFolder tmp


    void 'load from file'() {
        given:
            File fixture = tmp.newFile('items.json')
            fixture.text = JsonOutput.toJson(new Item(id: 'Id', name: 'Name', description: 'Description'))
            Dru dru = Dru.create(this)
            dru.load {
                from fixture, {
                    map { to Item }
                }
            }
        expect:
            dru.findAllByType(Item).size() == 1
            new FileSource(this, fixture).toString().startsWith('FileSource')
    }

    void 'load from file - no mapping'() {
        when:
            File fixture = tmp.newFile('items.json')
            fixture.text = JsonOutput.toJson(new Item(id: 'Id', name: 'Name', description: 'Description'))
            Dru dru = Dru.create(this)
            dru.load {
                from fixture
            }
        then:
            noExceptionThrown()
    }

    void 'load non existing file'() {
        given:
            File fixture = tmp.newFile('items.json')
            fixture.delete()

        when:
            Dru dru = Dru.create(this)
            dru.load {
                from fixture, {
                    map { to Item }
                }
            }
        then:
            thrown(IllegalArgumentException)
    }

}
