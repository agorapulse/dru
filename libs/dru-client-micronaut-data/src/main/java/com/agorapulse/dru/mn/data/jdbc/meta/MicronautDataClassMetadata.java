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

import com.agorapulse.dru.persistence.meta.ClassMetadata;
import com.agorapulse.dru.persistence.meta.PropertyMetadata;
import io.micronaut.data.model.runtime.RuntimePersistentEntity;
import io.micronaut.data.model.runtime.RuntimePersistentProperty;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
public class MicronautDataClassMetadata implements ClassMetadata {

    private final RuntimePersistentEntity entity;
    private final Class type;

    public MicronautDataClassMetadata(RuntimePersistentEntity entity, Class type) {
        this.entity = entity;
        this.type = type;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<PropertyMetadata> getPersistentProperties() {
        Stream<RuntimePersistentProperty> stream = entity.getPersistentProperties().stream();
        return stream.map(MicronautDataPropertyMetadata::new).collect(Collectors.toList());
    }

    @Override
    public PropertyMetadata getPersistentProperty(String name) {
        RuntimePersistentProperty property = entity.getPropertyByName(name);

        if (property == null) {
            return null;
        }

        return new MicronautDataPropertyMetadata(property);
    }

    @Override
    public Object getId(Map<String, Object> fixture) {
        return getIdPropertyNames().stream().findFirst().map(fixture::get).orElse(null);
    }

    @Override
    public Set<String> getIdPropertyNames() {
        RuntimePersistentProperty identity = entity.getIdentity();

        if (identity == null) {
            return Collections.emptySet();
        }

        return Collections.singleton(identity.getName());
    }
}
