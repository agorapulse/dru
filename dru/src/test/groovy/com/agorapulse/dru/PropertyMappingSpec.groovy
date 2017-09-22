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
            propertyMapping.ignore('five')
        then:
            typeMapping.ignored.size() == 5
            mapTypeMapping.ignored.size() == 5

    }

}
