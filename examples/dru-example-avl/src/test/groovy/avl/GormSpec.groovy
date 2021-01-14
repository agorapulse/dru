package avl

import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading persons.
 */
class GormSpec extends Specification implements DataTest {

    // tag::plan[]
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

    void 'entities can be accessed from data set and using GORM methods'() {
        expect:
            dru.findAllByType(Agent).size() == 2
            dru.findByTypeAndOriginalId(Agent, 12345).manager.name == 'Silas Ramsbottom'
        and:
            Agent.count() == 2
            Agent.findByName('Silas Ramsbottom').id != 12345
    }
    // end::plan[]
}
