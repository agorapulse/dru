package avl

import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading persons.
 */
class WhenLoadedSpec extends Specification implements DataTest {

    private static final List<Map<String, Object>> AGENTS = [
        [
            id           : 12345,
            name         : 'Felonius Gru',
            bio          : 'Born from the family with long line of villainy and formerly the world\'s greatest villain.',
            securityLevel: 2,
            manager      : [
                id  : 101,
                name: 'Silas Ramsbottom'
            ]
        ]
    ]

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('AGENTS') {
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

    void 'calling when loaded hook'() {
        when:
            int count = 0
            dru.load {
                whenLoaded {
                    count++
                }
            }
        then:
            count == 1                                                                  // <1>
        when:
            dru.loaded()
        then:
            count == 2                                                                  // <2>
    }
    // end::plan[]
}
