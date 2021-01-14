package com.agorapulse.dru.parser

import spock.lang.Specification
import com.agorapulse.dru.DefaultSource

/**
 * Sanity spec for parsers utility.
 */
class ParsersSpec extends Specification {

    void 'parsers constructor is hidden'() {
        expect:
            new Parsers()
    }

    void 'if there is no parser, exception is thrown'() {
        when:
            Parsers.findParser(new DefaultSource(this, 'xyz'))
        then:
            thrown(IllegalArgumentException)
    }
}
