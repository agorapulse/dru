package com.agorapulse.dru.gorm.persistence

import com.agorapulse.dru.parser.Parser
import com.agorapulse.dru.gorm.persistence.meta.GormClassMetadata
import com.agorapulse.dru.persistence.AbstractCacheableClient
import com.agorapulse.dru.persistence.Client
import com.agorapulse.dru.persistence.ClientFactory
import com.agorapulse.dru.persistence.meta.ClassMetadata
import grails.core.GrailsDomainClass
import grails.testing.gorm.DataTest
import org.grails.datastore.gorm.GormEntity

/**
 * Client for GORM.
 */
class Gorm extends AbstractCacheableClient {

    static class Factory implements ClientFactory {
        final int index = 10000

        @Override
        @SuppressWarnings([
            'Instanceof',
            'LineLength',
            'SystemErrPrint',
        ])
        boolean isSupported(Object unitTest) {
            boolean supported = unitTest instanceof DataTest
            if (!supported) {
                if (!unitTest.class.getAnnotation(NoGorm)) {
                    System.err.println("Gorm Dru client is on the classpath but ${unitTest.getClass()} does not implement $DataTest.name. Please, add @NoGorm annotation to hide this message if this is intended.")
                }
            }
            return supported
        }

        @Override
        Client newClient(Object unitTest) {
            return new Gorm(unitTest as DataTest)
        }
    }

    private final DataTest dataTest
    private final Set<Class> mockedDomainClasses = new LinkedHashSet<>()

    private Gorm(DataTest dataTest) {
        this.dataTest = dataTest
    }

    @Override
    boolean computeIsSupported(Class type) {
        return GormEntity.isAssignableFrom(type)
    }

    @Override
    protected ClassMetadata createClassMetadata(Class type) {
        ensureMocked(type)
        return new GormClassMetadata(dataTest.grailsApplication.getArtefact('Domain', type.name) as GrailsDomainClass)
    }

    @Override
    <T> T save(T object) {
        return object.save(failOnError: true)
    }

    @Override
    <T> T addTo(T object, String association, Object other) {
        return object.addTo(association, other)
    }

    @Override
    <T> T newInstance(Parser parser, Class<T> type, Map<String, Object> payload) {
        return type.newInstance(payload)
    }

    void ensureMocked(Class domainClass) {
        if (!mockedDomainClasses.contains(domainClass)) {
            mockedDomainClasses.add(domainClass)
            dataTest.mockDomain(domainClass)
        }
    }
}
