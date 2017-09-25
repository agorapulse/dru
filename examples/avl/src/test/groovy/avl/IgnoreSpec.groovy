package avl

import com.agorapulse.dru.Dru
import org.junit.Rule
import spock.lang.Specification

/**
 * Testing value override.
 */
class IgnoreSpec extends Specification {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('item.json') {
            map {
                to (Item) {
                    ignore 'owner'
                }
            }
        }
    }

    void 'owner does is not present in the report'() {
        when:
            dru.load()
        then:
            dru.report.empty
    }
    // end::plan[]
}
