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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

final class PropertyMappings implements Iterable<PropertyMapping> {

    private final String parentPath;

    PropertyMappings(String parentPath) {
        this.parentPath = parentPath;
    }

    @Override
    public Iterator<PropertyMapping> iterator() {
        return mappings.values().iterator();
    }

    @SuppressWarnings("unchecked")
    public PropertyMapping find(String propertyName) {
        return mappings.get(propertyName);
    }

    @SuppressWarnings("unchecked")
    public PropertyMapping findOrCreate(String propertyName) {
        PropertyMapping mapping = mappings.get(propertyName);
        if (mapping == null) {
            PropertyMapping newMapping = new PropertyMapping(parentPath, propertyName);
            mappings.put(propertyName, newMapping);
            return newMapping;
        }
        return mapping;
    }

    private final Map<String, PropertyMapping> mappings = new LinkedHashMap<>();

}
