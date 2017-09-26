package com.agorapulse.dru.gorm.persistence

import org.grails.datastore.gorm.GormEntity
import spock.lang.Specification

/**
 * Test for GORM client.
 */
class GormSpec extends Specification {

    void 'throws exception if the unit test is not DataTest'() {
        when:
            new Gorm.Factory().newClient(this).getClassMetadata(GormEntity)
        then:
            thrown(IllegalStateException)
    }

}
