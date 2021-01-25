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
 * Type mapping tests.
 */
class TypeMappingSpec extends Specification {

    void 'test to string'() {
        expect:
            new TypeMapping<Map>('', Map).toString() == 'TypeMapping[Map]'
    }

    @SuppressWarnings([
        'ExplicitCallToAndMethod',
        'UnnecessaryObjectReferences',
    ])
    void 'test mapping'() {
        when:
            TypeMapping<PojoTester> mapping = new TypeMapping<PojoTester>('', PojoTester)
            mapping.ignore {
                numericalValue
            }
            mapping.ignore(['first', 'second'])
            mapping.ignore('third', 'fourth')
            mapping.when { foo == 'bar' }
            mapping.and { zoo == 'lane' }
            mapping.map(['numbers', 'number']) { to Object }
        then:
            mapping.map('foo').path == 'foo'
            mapping.propertyMappings.size() == 3
            mapping.conditions.size() == 2
            mapping.ignored.size() == 5
    }

}
