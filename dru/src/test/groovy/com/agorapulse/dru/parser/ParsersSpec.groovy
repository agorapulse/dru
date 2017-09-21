package com.agorapulse.dru.parser

import spock.lang.Specification

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
            Parsers.findParser('foo.bar')
        then:
            thrown(IllegalArgumentException)
    }
}
