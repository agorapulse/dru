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
