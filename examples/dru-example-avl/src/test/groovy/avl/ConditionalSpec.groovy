package avl

import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading persons.
 */
class ConditionalSpec extends Specification implements DataTest {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('persons.json') {
            map {
                to (Agent) {
                    when { it.type == 'agent' }
                    defaults { securityLevel = 1 }
                }
                to (Villain) {
                    when { it.type == 'villain' }
                }
            }
        }
    }

    void 'entities are mapped to proper types'() {
        expect:
            dru.findAllByType(Agent).size() == 1
            dru.findAllByType(Villain).size() == 1
    }
    // end::plan[]
}
