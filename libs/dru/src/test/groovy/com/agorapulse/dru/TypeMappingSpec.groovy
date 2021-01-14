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
