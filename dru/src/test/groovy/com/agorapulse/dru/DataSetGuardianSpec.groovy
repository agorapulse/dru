package com.agorapulse.dru

import org.junit.Rule
import spock.lang.Specification

/**
 * Tests fof DataSetGuardian
 */
class DataSetGuardianSpec extends Specification {

    @Rule Dru dru = Dru.steal(this)

    DataSet dataSet
    DataSet guardian

    void setup() {
        dataSet = dru.load()
        guardian = DataSetGuardian.guard(dataSet)
    }

    void 'forwards methods'() {
        given:
            DataSet mock = Mock(DataSet)
            DataSet guarded = DataSetGuardian.guard(mock)

        when:
            guarded.loaded()
        then:
            1 * mock.loaded()

        when:
            guarded.changed()
        then:
            1 * mock.changed()

        when:
            guarded.report
        then:
            1 * mock.report

        when:
            guarded.load { }
        then:
            1 * mock.load(_ as Closure)

        when:
            guarded.load(Dru.prepare { }, Dru.prepare { })
        then:
            1 * mock.load(*_)
    }

    void 'guradian is not the same as dru'() {
        expect:
            !(dataSet.is(guardian))

        when:
            DataSet same = DataSetGuardian.guard(guardian)
        then:
            same.is(guardian)
    }

    void 'cannot work with immutable objects by default'() {
        when:
            guardian.add(new ImmutableObject('foo'))
        then:
            thrown IllegalArgumentException
    }

    void 'can work with nulls'() {
        expect:
            guardian.findByType(ImmutableObject) == null
    }

    void 'add with manual id'() {
        when:
            guardian.add(new Book(title: 'It'), 'it')
        then:
            guardian.findByTypeAndOriginalId(Book, 'it')
    }

}

class ImmutableObject {
    private final String value

    ImmutableObject(String value) {
        this.value = value
    }
}
