package com.agorapulse.dru.gorm.persistence.meta

import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty

/**
 * Describes GORM domain class.
 */
class GormClassMetadata implements ClassMetadata {

    private final PersistentEntity persistentEntity

    GormClassMetadata(PersistentEntity persistentEntity) {
        this.persistentEntity = persistentEntity
    }

    @Override
    Iterable<PropertyMetadata> getPersistentProperties() {
        return persistentEntity.persistentProperties.collect { new GormPropertyMetadata(it) }
    }

    @Override
    PropertyMetadata getPersistentProperty(String name) {
        PersistentProperty property = persistentEntity.getPropertyByName(name)
        if (!property) {
            return null
        }
        return new GormPropertyMetadata(property)
    }

    @Override
    Class getType() {
        return persistentEntity.javaClass
    }

    @Override
    Serializable getId(Map<String, Object> fixture) {
        PersistentProperty identifier = persistentEntity.identity
        assert identifier
        return fixture[identifier.name].asType(identifier.type) as Serializable
    }

    @Override
    Set<String> getIdPropertyNames() {
        return [persistentEntity.identity?.name].grep().toSet()
    }
}
