package avl

import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Testing retrieving just single value.
 */
class JustSpec extends Specification implements DataTest {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('missionLogEntry.json') {
            map {
                to (MissionLogEntry) {
                    map ('agent') {
                        to (agentId: Agent) {
                            just { id }
                            defaults {
                                securityLevel = 1
                            }
                        }
                    }
                }
            }
        }
    }

    void 'mission log entry has agent id assigned'() {
        expect:
            dru.findByType(Agent)
            dru.findByType(MissionLogEntry).agentId == 1
    }
    // end::plan[]
}
