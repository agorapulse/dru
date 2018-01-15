package com.agorapulse.dru.gorm.persistence.meta

import com.agorapulse.dru.persistence.meta.AbstractPropertyMetadata
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.model.types.Association
import org.grails.datastore.mapping.model.types.Basic
import org.grails.datastore.mapping.model.types.Identity
import org.grails.datastore.mapping.model.types.ManyToMany
import org.grails.datastore.mapping.model.types.ManyToOne
import org.grails.datastore.mapping.model.types.OneToMany
import org.grails.datastore.mapping.model.types.OneToOne
import org.grails.datastore.mapping.model.types.Simple

/**
 * Describes GORM domain class's persistent property.
 */
class GormPropertyMetadata extends AbstractPropertyMetadata {

    private final PersistentProperty persistentProperty

    GormPropertyMetadata(PersistentProperty persistentProperty) {
        this.persistentProperty = persistentProperty
    }

    @Override
    String getName() {
        return persistentProperty.name
    }

    @Override
    Class getType() {
        return persistentProperty.type
    }

    @Override
    Class getReferencedPropertyType() {
        if (persistentProperty instanceof Association && persistentProperty.associatedEntity) {
            return persistentProperty.associatedEntity.javaClass
        }
        if (persistentProperty instanceof Simple) {
            return persistentProperty.type
        }
        if (persistentProperty instanceof Basic) {
            return persistentProperty.componentType
        }
        return persistentProperty.type
    }

    @Override
    boolean isPersistent() {
        return persistentProperty && !(persistentProperty instanceof Identity)
    }

    @Override
    boolean isOneToMany() {
        return persistentProperty instanceof OneToMany
    }

    @Override
    boolean isManyToOne() {
        return persistentProperty instanceof ManyToOne
    }

    @Override
    boolean isManyToMany() {
        return persistentProperty instanceof ManyToMany
    }

    @Override
    boolean isOneToOne() {
        return persistentProperty instanceof OneToOne
    }

    @Override
    boolean isAssociation() {
        return persistentProperty instanceof Association
    }

    @Override
    boolean isOwningSide() {
        return persistentProperty instanceof Association && persistentProperty.owningSide
    }

    @Override
    String getReferencedPropertyName() {
        if (persistentProperty instanceof Association) {
            if (persistentProperty.referencedPropertyName) {
                return persistentProperty.referencedPropertyName
            }

            Association association = persistentProperty as Association

            if (association.associatedEntity) {
                Association referencedProperty = association.associatedEntity.associations.find { it.associatedEntity == persistentProperty.owner }

                if (referencedProperty) {
                    return referencedProperty.name
                }
            }
        }
        return null
    }

    @Override
    boolean isEmbedded() {
        return persistentProperty instanceof Association && persistentProperty.embedded
    }

    @Override
    boolean isBasicCollectionType() {
        return persistentProperty instanceof Basic
    }
}
