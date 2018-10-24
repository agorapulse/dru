package avl

import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading persons.
 */
class ListenersSpec extends Specification implements DataTest {

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

    void 'calling on change hook'() {
        when:
            int count = 0
            dru.load {
                onChange {
                    count++
                }
            }
        then:
            count == 0

        when:
            dru.changed()
        then:
            count == 1

        when:
            Agent gru = new Agent(id: 23456, name: 'Gru')
            dru.add(gru)

        then:
            count == 2

        when:
            dru.remove(gru)
        then:
            count == 3
    }
}
