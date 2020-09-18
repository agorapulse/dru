package avl

import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading persons.
 */
@SuppressWarnings([
    'UnusedPrivateField',
    'SpaceAroundMapEntryColon',
    'NestedBlockDepth',
    'TrailingComma',
])
class ReflectionSpec extends Specification implements DataTest {

    // tag::plan[]
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

    void 'nested properties are mapped'() {
        expect:
            dru.findAllByType(Agent).size() == 2
            dru.findByTypeAndOriginalId(Agent, 12345).manager.name == 'Silas Ramsbottom'
    }
    // end::plan[]
}
