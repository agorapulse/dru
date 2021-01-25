/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
@SuppressWarnings('Instanceof')
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
    @SuppressWarnings('CouldBeSwitchStatement')
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
