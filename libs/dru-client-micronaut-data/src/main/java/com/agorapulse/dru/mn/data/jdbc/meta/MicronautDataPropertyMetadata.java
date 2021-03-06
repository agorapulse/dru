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
package com.agorapulse.dru.mn.data.jdbc.meta;

import com.agorapulse.dru.persistence.meta.AbstractPropertyMetadata;
import com.agorapulse.dru.persistence.meta.PropertyMetadata;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.model.Embedded;
import io.micronaut.data.model.runtime.RuntimeAssociation;
import io.micronaut.data.model.runtime.RuntimePersistentProperty;

import java.util.Optional;

public class MicronautDataPropertyMetadata extends AbstractPropertyMetadata implements PropertyMetadata {

    private final RuntimePersistentProperty property;

    public MicronautDataPropertyMetadata(RuntimePersistentProperty property) {
        this.property = property;
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public Class getType() {
        return property.getType();
    }

    @Override
    public boolean isOwningSide() {
        // TODO: double check this is ok
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class getReferencedPropertyType() {
        if (property instanceof RuntimeAssociation) {
            RuntimeAssociation association = (RuntimeAssociation) property;
            return association.getAssociatedEntity().getIntrospection().getBeanType();
        }
        return property.getType();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String getReferencedPropertyName() {
        if (property instanceof RuntimeAssociation) {
            RuntimeAssociation association = (RuntimeAssociation) property;
            Optional<RuntimeAssociation> inverseSide = association.getInverseSide();
            return inverseSide.map(RuntimeAssociation::getName).orElse(null);
        }
        return null;
    }

    @Override
    public boolean isBasicCollectionType() {
        // TODO: double check this is ok
        return false;
    }

    @Override
    public boolean isPersistent() {
        // TODO: double check this is ok
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isOneToMany() {
        if (property instanceof RuntimeAssociation) {
            RuntimeAssociation association = (RuntimeAssociation) property;
            return association.getKind() == Relation.Kind.ONE_TO_MANY;
        }
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isManyToOne() {
        if (property instanceof RuntimeAssociation) {
            RuntimeAssociation association = (RuntimeAssociation) property;
            return association.getKind() == Relation.Kind.MANY_TO_ONE;
        }
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isManyToMany() {
        if (property instanceof RuntimeAssociation) {
            RuntimeAssociation association = (RuntimeAssociation) property;
            return association.getKind() == Relation.Kind.MANY_TO_MANY;
        }
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean isOneToOne() {
        if (property instanceof RuntimeAssociation) {
            RuntimeAssociation association = (RuntimeAssociation) property;
            return association.getKind() == Relation.Kind.ONE_TO_ONE;
        }
        return false;
    }

    @Override
    public boolean isAssociation() {
        return property instanceof RuntimeAssociation;
    }

    @Override
    public boolean isEmbedded() {
        return property instanceof Embedded;
    }
}
