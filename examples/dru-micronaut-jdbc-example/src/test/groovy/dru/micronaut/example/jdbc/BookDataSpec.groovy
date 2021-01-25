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
package dru.micronaut.example.jdbc

import com.agorapulse.dru.Dru
import io.micronaut.context.ApplicationContext
import io.micronaut.context.ApplicationContextProvider
import io.micronaut.test.annotation.MicronautTest
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
class BookDataSpec extends Specification implements ApplicationContextProvider {        // <1>

    private static final List<Map<String, Object>> BOOKS = [                            // <2>
        [
            id    : 12345,
            title : 'It',
            pages : 1116,
            author: [id: 666],
        ],
        [
            id    : 12666,
            title : 'The Shining',
            pages : 659,
            author: [id: 666],
        ],
    ]

    @AutoCleanup Dru dru = Dru.create {                                                 // <3>
        from 'BOOKS', {
            map {
                to Book
            }
        }
    }

    @Inject ApplicationContext applicationContext                                       // <4>
    @Inject BookRepository bookRepository                                               // <5>

    void setup() {
        dru.load()                                                                      // <6>
    }

    void 'load books'() {
        expect:
            bookRepository.count() == 2                                                 // <7>
    }

}
