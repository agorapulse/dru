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

import org.junit.Rule
import spock.lang.Specification

import java.util.function.Consumer

/**
 * Tests fof DataSetGuardian
 */
class DataSetGuardianSpec extends Specification {

    @Rule Dru dru = Dru.steal(this)

    DataSet dataSet
    DataSet guardian

    void setup() {
        dataSet = dru.load()
        guardian = DataSetGuardian.guard(dataSet)
    }

    void 'forwards methods'() {
        given:
            DataSet mock = Mock(DataSet)
            DataSet guarded = DataSetGuardian.guard(mock)

        when:
            guarded.loaded()
        then:
            1 * mock.loaded()

        when:
            guarded.changed()
        then:
            1 * mock.changed()

        when:
            guarded.report
        then:
            1 * mock.report

        when:
            guarded.load { }
        then:
            1 * mock.load(_, _ as Consumer)

        when:
            guarded.load(Dru.prepare { }, Dru.prepare { })
        then:
            1 * mock.load(*_)
    }

    void 'guradian is not the same as dru'() {
        expect:
            !(dataSet.is(guardian))

        when:
            DataSet same = DataSetGuardian.guard(guardian)
        then:
            same.is(guardian)
    }

    void 'cannot work with immutable objects by default'() {
        when:
            guardian.add(new ImmutableObject('foo'))
        then:
            thrown IllegalArgumentException
    }

    void 'can work with nulls'() {
        expect:
            guardian.findByType(ImmutableObject) == null
    }

    void 'add with manual id'() {
        when:
            guardian.add(new Book(title: 'It'), 'it')
        then:
            guardian.findByTypeAndOriginalId(Book, 'it')
    }

}

class ImmutableObject {
    private final String value

    ImmutableObject(String value) {
        this.value = value
    }
}
