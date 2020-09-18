package avl

import com.agorapulse.dru.Dru
import com.agorapulse.dru.PreparedDataSet

/**
 * Agents data set.
 */
@SuppressWarnings('FieldName')
class AgentsDataSet {
    public static final PreparedDataSet agentsMapping = Dru.prepare {                   // <1>
        any (Agent) {
            map ('manager') {
                to (Agent)
            }
            defaults {
                securityLevel = 1
            }
        }
    }

    public static final PreparedDataSet agents = Dru.prepare {                          // <2>
        include agentsMapping                                                           // <3>
        from ('agents.json') {
            map {
                to (Agent)
            }
        }
    }
}
