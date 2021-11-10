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
package com.agorapulse.dru.parser.sql

import com.agorapulse.dru.Dru
import org.h2.jdbcx.JdbcDataSource
import spock.lang.Specification

import javax.sql.DataSource
import java.security.SecureRandom

class BasicSqlParserSpec extends Specification implements DataSourceProvider {          // <1>

    DataSource dataSource = new JdbcDataSource(                                         // <2>
        URL: 'jdbc:h2:mem:default' + new SecureRandom().nextInt(),
        user: 'sa',
        password: 'sa',
    )

    Dru dru = Dru.create {
        from 'books.sql', {                                                             // <3>
            map 'BOOK', {                                                               // <4>
                to Book, {
                    map 'PAGES', { to (pages: Integer) }                                // <5>
                    map 'TITLE', { to (title: String) }
                }
            }
        }
    }

    void setup() {
        dru.load()
    }

    void 'load using sql into object'() {
        when:
            List<Book> books = dru.findAllByType(Book)                                  // <6>
        then:
            books.size() == 2
            books.first().title == 'It'
            books.first().pages == 1116
            books.last().title == 'The Shining'
            books.last().pages == 659
    }

}
