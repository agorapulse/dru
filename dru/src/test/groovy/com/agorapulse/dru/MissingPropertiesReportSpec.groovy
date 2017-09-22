package com.agorapulse.dru

import spock.lang.Specification

/**
 * Test for missing properties report.
 */
class MissingPropertiesReportSpec extends Specification {

    void 'test empty report'() {
        expect:
            new MissingPropertiesReport().toString().contains('DRU SAYS: GOOD JOB, NOTHING IGNORED ')
    }

    void 'test non-empty report'() {
        when:
            MissingPropertiesReport report = new MissingPropertiesReport()
            report.add(MissingProperty.create('foo', 'bar', [foo: 'bar'], Map))
            report.add(MissingProperty.create('foo', 'baz', [foo: 'bar'], Map))
            report.add(MissingProperty.create('zoo', 'lane', [foo: 'bar'], List))
        then:
            report.toString().contains('‖ Map      | bar      | foo      | [foo:bar] ‖')
    }
}
