package avl

import com.agorapulse.dru.Dru
import grails.testing.mixin.integration.Integration
import org.junit.Rule
import org.springframework.test.annotation.Rollback
import spock.lang.Specification

/**
 * Test loading persons.
 */
// tag::plan[]
@Rollback
@Integration                                                                            // <1>
class GormIntegrationSpec extends Specification {

    @Rule Dru dru = Dru.plan {
        from ('agents.json') {
            map {
                to (Agent) {
                    map ('manager') {
                        to (Agent)
                    }
                }
            }
        }

        any (Agent) {
            defaults { securityLevel = 1 }
        }
    }

    void setup() {
        Agent.withNewSession { dru.load() }                                             // <2>
    }

    void 'entities can be accessed from data set and using GORM methods'() {
        expect:
            dru.findAllByType(Agent).size() == 2
            dru.findByTypeAndOriginalId(Agent, 12345).manager.name == 'Silas Ramsbottom'
        and:
            Agent.withNewSession { Agent.count() } == 2
            Agent.withNewSession { Agent.findByName('Silas Ramsbottom').id } != 12345
    }
}
// end::plan[]
