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
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading persons.
 */
class PojoSpec extends Specification {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('library.json') {
            map {
                to (Library)
            }
        }
    }

    void 'library is loaded'() {
        expect:
            dru.findAllByType(Library).size() == 1
            dru.findAllByType(Book).size() == 2
    }
    // end::plan[]
}
