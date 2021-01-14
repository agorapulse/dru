package avl

import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading persons using YAML parser.
 */
class YamlSpec extends Specification implements DataTest {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('agents.yml') {
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
}
