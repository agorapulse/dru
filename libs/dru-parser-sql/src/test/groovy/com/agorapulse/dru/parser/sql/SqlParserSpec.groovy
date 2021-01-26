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
import groovy.sql.Sql
import spock.lang.Specification

import javax.sql.DataSource
import org.h2.jdbcx.JdbcDataSource

class SqlParserSpec extends Specification implements DataSourceProvider {

    DataSource dataSource = new JdbcDataSource(
        URL: 'jdbc:h2:mem:default',
        user: 'sa',
        password: 'sa',
    )

    void 'load using sql'() {
        given:
            Dru dru = Dru.create {
                from 'books.sql'
            }
        when:
            dru.load()
        and:
            Sql sql = new Sql(dataSource)
        then:
            sql.rows('select * from `book`').size == 2
    }

    void 'terminator always required'() {
        when:
            Dru.create {
                from  'missing-terminator.sql'
            }.load()
        then:
            thrown(IllegalArgumentException)
    }

    void 'sql exception is rethrown'() {
        when:
            Dru.create {
                from 'sql-exception.sql'
            }.load()
        then:
            thrown(IllegalArgumentException)
    }

}
