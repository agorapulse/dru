package avl

import com.agorapulse.dru.DataSet
import com.agorapulse.dru.Dru
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading agents.
 */
class DataSetSpec extends Specification implements DataTest {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        include AgentsDataSet.agents
    }

    void 'agents get loaded from data set using prepare and include'() {
        expect:
            dru.findAllByType(Agent).size() == 2
            dru.findByTypeAndOriginalId(Agent, 12345).manager.name == 'Silas Ramsbottom'
    }

    void 'agents get loaded from data set using load'() {
        given:
            DataSet dataSet = Dru.steal(this).load(AgentsDataSet.agents)
        expect:
            dataSet.findAllByType(Agent).size() == 2
            dataSet.findByTypeAndOriginalId(Agent, 12345).manager.name == 'Silas Ramsbottom'
    }
    // end::plan[]
}
