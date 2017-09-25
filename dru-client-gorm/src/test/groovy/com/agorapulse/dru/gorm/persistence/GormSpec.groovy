package com.agorapulse.dru.gorm.persistence

import com.agorapulse.dru.Dru
import spock.lang.Specification
import uk.org.lidalia.slf4jtest.TestLogger
import uk.org.lidalia.slf4jtest.TestLoggerFactory

/**
 * Test for GORM client.
 */
class GormSpec extends Specification {

    void 'prints not supported if spec is not DataTest'() {
        given:
            TestLogger logger = TestLoggerFactory.getTestLogger(Gorm.Factory)
        when:
            Dru.steal(this)
        then:
            logger.allLoggingEvents.any {
                it.message.contains('Gorm Dru client is on the classpath')
            }
    }

}
