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
package com.agorapulse.dru.persistence.meta;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CachedClassMetadata implements ClassMetadata {

    private final Class type;
    private final Map<String, PropertyMetadata> persistentProperties;
    private final ClassMetadata original;
    private Set<String> idPropertyNames;

    public CachedClassMetadata(ClassMetadata original) {
        this.type = original.getType();
        this.persistentProperties = new LinkedHashMap<>();
        for (PropertyMetadata metadata : original.getPersistentProperties()) {
            this.persistentProperties.put(metadata.getName(), new CachedPropertyMetadata(metadata));
        }
        this.original = original;
        this.idPropertyNames = new HashSet<>(original.getIdPropertyNames());
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public Iterable<PropertyMetadata> getPersistentProperties() {
        return persistentProperties.values();
    }

    @Override
    public PropertyMetadata getPersistentProperty(String name) {
        PropertyMetadata metadata = persistentProperties.get(name);
        if (metadata != null) {
            return metadata;
        }

        metadata = getOriginal().getPersistentProperty(name);

        if (metadata != null) {
            persistentProperties.put(name, metadata);
        }

        return metadata;
    }

    @Override
    public Object getId(Map<String, Object> fixture) {
        return original.getId(fixture);
    }

    @Override
    public Set<String> getIdPropertyNames() {
        return new HashSet<>(idPropertyNames);
    }

    public ClassMetadata getOriginal() {
        return original;
    }
}
