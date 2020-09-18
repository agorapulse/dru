package avl

import com.agorapulse.dru.Dru
import org.junit.Rule
import spock.lang.Specification

/**
 * Testing value override.
 */
class OverrideValueSpec extends Specification {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('item.json') {
            map {
                to (Item) {
                    overrides {
                        description = "Description for $it.name"
                    }
                }
            }
        }
    }

    void 'entities can be access from the data set'() {
        when:
            Item item = dru.findByTypeAndOriginalId(Item, ID)
        then:
            item
            item.name == 'PX-41'
            item.description == 'Description for PX-41'
    }
    // end::plan[]

    private static final String ID = '050e4fcf-158d-4f44-9b8b-a6ba6809982e:PX-41'
}
