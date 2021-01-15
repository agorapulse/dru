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
package com.agorapulse.dru;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

final class TypeMappings implements Iterable<TypeMapping> {

    @Override
    public Iterator<TypeMapping> iterator() {
        return mappings.values().iterator();
    }

    public TypeMapping findByType(Class type) {
        return mappings.get(type);
    }

    @SuppressWarnings("unchecked")
    public TypeMapping find(Object fixture) {
        // type mapping works only for maps at the moment
        if (!(fixture instanceof Map)) {
            return find(Collections.singletonMap("value", fixture));
        }

        for (TypeMapping typeMapping : mappings.values()) {
            if (!typeMapping.getConditions().isEmpty()) {
                for (Predicate condition : (Iterable<Predicate>) typeMapping.getConditions()) {
                    if (condition.test(fixture)) {
                        return typeMapping;
                    }
                }
            } else {
                return typeMapping;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void applyOverrides(Class type, Object destination, Object source) {
        TypeMapping mapping = mappings.get(type);

        if (mapping != null) {
            mapping.getOverrides().apply(destination, source);
        }


        if (type.getSuperclass() != null) {
            applyOverrides(type.getSuperclass(), destination, source);
        }

        for (Class it : type.getInterfaces()) {
            applyOverrides(it, destination, source);
        }

    }

    @SuppressWarnings("unchecked")
    public <T> TypeMapping<T> findOrCreate(Class<T> type, String path) {
        TypeMapping<T> mapping = mappings.get(type);
        if (mapping == null) {
            TypeMapping<T> newMapping = new TypeMapping<>(path, type);
            mappings.put(type, newMapping);
            return newMapping;
        }
        return mapping;
    }

    @SuppressWarnings("unchecked")
    public void applyDefaults(Class<?> type, Object destination, Object source) {
        TypeMapping mapping = mappings.get(type);

        if (mapping != null) {
            mapping.getDefaults().apply(destination, source);
        }


        if (type.getSuperclass() != null) {
            applyDefaults(type.getSuperclass(), destination, source);
        }

        for (Class it : type.getInterfaces()) {
            applyDefaults(it, destination, source);
        }

    }

    @SuppressWarnings("unchecked")
    public boolean isIgnored(Class<?> type, String propertyName) {
        TypeMapping mapping = mappings.get(type);

        if (mapping != null) {
            if (mapping.getIgnored().contains(propertyName)) {
                return true;
            }
        }

        if (type.getSuperclass() != null && isIgnored(type.getSuperclass(), propertyName)) {
            return true;
        }

        for (Class i : type.getInterfaces()) {
            if (isIgnored(i, propertyName)) {
                return true;
            }
        }

        return false;
    }

    public void addAll(TypeMappings typeMappings) {
        this.mappings.putAll(typeMappings.mappings);
    }

    private final Map<Class, TypeMapping> mappings = new LinkedHashMap<>();
}
