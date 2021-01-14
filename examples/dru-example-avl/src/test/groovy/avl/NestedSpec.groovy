package avl

import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading persons.
 */
class NestedSpec extends Specification implements DataTest {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('agents.json') {
            map {
                to (Agent) {
                    map ('manager') {
                        to (Agent) {
                            defaults { securityLevel = 1 }
                        }
                    }
                }
            }
        }
    }

    void 'nested properties are mapped'() {
        expect:
            dru.findAllByType(Agent).size() == 2
            dru.findByTypeAndOriginalId(Agent, 12345).manager.name == 'Silas Ramsbottom'
    }
    // end::plan[]

    // tag::reuse[]
    @Rule Dru reuse = Dru.plan {
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
    // end::reuse[]
}
