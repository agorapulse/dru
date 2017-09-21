package avl

import spock.lang.Specification

class AgentSpec extends Specification {

    void 'test is novice'() {
        expect:
            new Agent(securityLevel: 1).novice
            new Agent(securityLevel: 2).novice
            new Agent(securityLevel: 3).novice
            new Agent(securityLevel: 4).novice
            !new Agent(securityLevel: 5).novice
            !new Agent(securityLevel: 10).novice
    }

}
