package com.agorapulse.dru

import spock.lang.Specification

/**
 * Type mapping tests.
 */
class TypeMappingsSpec extends Specification {

    void 'find or create always returns same instance'() {
        given:
            TypeMappings mappings = new TypeMappings()
        when:
            TypeMapping<PojoTester> first = mappings.findOrCreate(PojoTester, 'foo')
            TypeMapping<PojoTester> second = mappings.findOrCreate(PojoTester, 'foo')
        then:
            first.is(second)
    }

}
