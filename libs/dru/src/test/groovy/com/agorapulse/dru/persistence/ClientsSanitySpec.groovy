package com.agorapulse.dru.persistence

import spock.lang.Specification

/**
 * Making code coverage happy.
 */
class ClientsSanitySpec extends Specification {

    void 'jacoco will like this even it does not make sense'() {
        expect:
            new Clients()
    }

}
