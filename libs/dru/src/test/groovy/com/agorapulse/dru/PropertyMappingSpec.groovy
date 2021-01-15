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
 * Tests for PropertyMapping
 */
class PropertyMappingSpec extends Specification {

    void 'only single map entry accepted by "to"'() {
        when:
            new PropertyMapping('foo.json', 'bar').to(foo: String, bar: Long)
        then:
            thrown(IllegalArgumentException)
    }

    void 'to string shows full path'() {
        expect:
            new PropertyMapping('foo.json', 'items').toString() == 'PropertyMapping[foo.json/items]'
    }

    void 'ignores are forwarded to type mappings'() {
        when:
            PropertyMapping propertyMapping = new PropertyMapping('root', 'path')
            propertyMapping.ignore(['one', 'two'])
            propertyMapping.ignore('three', 'four')
            propertyMapping.to(PojoTester)

            TypeMapping<PojoTester> typeMapping = propertyMapping.typeMappings.findByType(PojoTester)
        then:
            typeMapping.ignored.size() == 4
        when:
            propertyMapping.to(Map)
            TypeMapping<Map> mapTypeMapping = propertyMapping.typeMappings.findByType(Map)
        then:
            mapTypeMapping.ignored.size() == 4
        when:
            propertyMapping.ignore('five', 'six')
            propertyMapping.ignore(['seven', 'eight'])
        then:
            typeMapping.ignored.size() == 8
            mapTypeMapping.ignored.size() == 8

    }

}
