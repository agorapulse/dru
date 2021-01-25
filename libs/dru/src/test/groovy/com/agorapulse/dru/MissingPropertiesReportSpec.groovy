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

import spock.lang.Specification

/**
 * Test for missing properties report.
 */
class MissingPropertiesReportSpec extends Specification {

    void 'test empty report'() {
        expect:
            new MissingPropertiesReport().toString().contains('DRU SAYS: GOOD JOB, NOTHING IGNORED ')
    }

    void 'test non-empty report'() {
        when:
            MissingPropertiesReport report = new MissingPropertiesReport()
            report.add(MissingProperty.create('foo', 'bar', [foo: 'bar'], Map))
            report.add(MissingProperty.create('foo', 'baz', [foo: 'bar'], Map))
            report.add(MissingProperty.create('zoo', 'lane', [foo: 'bar'], List))
        then:
            report.toString().contains('‖ Map      | bar      | foo      | [foo:bar] ‖')
    }
}
