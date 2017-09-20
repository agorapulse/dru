package com.agorapulse.dru.gorm.persistence.meta

import com.agorapulse.dru.persistence.meta.PropertyMetadata
import grails.core.GrailsDomainClassProperty

/**
 * Describes GORM domain class's persistent property.
 */
class GormPropertyMetadata implements PropertyMetadata {

    private final GrailsDomainClassProperty grailsDomainClassProperty

    GormPropertyMetadata(GrailsDomainClassProperty grailsDomainClassProperty) {
        this.grailsDomainClassProperty = grailsDomainClassProperty
    }

    @Override
    String getName() {
        return grailsDomainClassProperty.name
    }

    @Override
    Class getType() {
        return grailsDomainClassProperty.type
    }

    @Override
    Class getReferencedPropertyType() {
        return grailsDomainClassProperty.referencedPropertyType
    }

    @Override
    boolean isPersistent() {
        return grailsDomainClassProperty.persistent
    }

    @Override
    boolean isOneToMany() {
        return grailsDomainClassProperty.oneToMany
    }

    @Override
    boolean isManyToOne() {
        return grailsDomainClassProperty.manyToOne
    }

    @Override
    boolean isManyToMany() {
        return grailsDomainClassProperty.manyToMany
    }

    @Override
    boolean isOneToOne() {
        return grailsDomainClassProperty.oneToOne
    }

    @Override
    boolean isAssociation() {
        return grailsDomainClassProperty.association
    }

    @Override
    boolean isOwningSide() {
        return grailsDomainClassProperty.owningSide
    }

    @Override
    String getReferencedPropertyName() {
        return grailsDomainClassProperty.referencedPropertyName
    }

    @Override
    boolean isEmbedded() {
        return grailsDomainClassProperty.embedded
    }

    @Override
    boolean isBasicCollectionType() {
        return grailsDomainClassProperty.basicCollectionType
    }

    @Override
    boolean isCollectionType() {
        return isBasicCollectionType() || isManyToMany() || isOneToMany()
    }
}
