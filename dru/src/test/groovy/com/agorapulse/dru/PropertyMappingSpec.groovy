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

}
