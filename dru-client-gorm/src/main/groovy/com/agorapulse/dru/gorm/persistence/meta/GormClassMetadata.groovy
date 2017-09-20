package com.agorapulse.dru.gorm.persistence.meta

import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import grails.core.GrailsDomainClass
import grails.core.GrailsDomainClassProperty

class GormClassMetadata implements ClassMetadata {

    private final GrailsDomainClass grailsDomainClass

    GormClassMetadata(GrailsDomainClass grailsDomainClass) {
        this.grailsDomainClass = grailsDomainClass
    }

    @Override
    Iterable<PropertyMetadata> getPersistentProperties() {
        return grailsDomainClass.persistentProperties.collect { new GormPropertyMetadata(it) }
    }

    @Override
    PropertyMetadata getPersistentProperty(String name) {
        GrailsDomainClassProperty property = grailsDomainClass.getPersistentProperty(name)
        if (!property) {
            return null
        }
        return new GormPropertyMetadata(property)
    }

    @Override
    Class getType() {
        return grailsDomainClass.clazz
    }

    @Override
    Serializable getId(Map<String, Object> fixture) {
        GrailsDomainClassProperty identifier = grailsDomainClass.getIdentifier()
        if (!identifier) {
            return null
        }
        return fixture[identifier.name] as Serializable
    }
}
