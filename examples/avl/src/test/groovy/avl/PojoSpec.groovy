package avl

import com.agorapulse.dru.Dru
import org.junit.Rule
import spock.lang.Specification

/**
 * Test loading persons.
 */
class PojoSpec extends Specification {

    // tag::plan[]
    @Rule Dru dru = Dru.plan {
        from ('library.json') {
            map {
                to (Library)
            }
        }
    }

    void 'library is loaded'() {
        expect:
            dru.findAllByType(Library).size() == 1
            dru.findAllByType(Book).size() == 2
    }
    // end::plan[]
}
