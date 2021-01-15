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
package com.agorapulse.dru.persistence

import com.agorapulse.dru.persistence.meta.CachedClassMetadata
import com.agorapulse.dru.persistence.meta.ClassMetadata
import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * Base class for clients with caching capabilities.
 */
abstract class AbstractCacheableClient implements Client {

    private final Map<Class, CachedClassMetadata> classMetadata = [:]
    private final Map<Class, Boolean> supportedClasses = [:]

    @Override
    final boolean isSupported(Class type) {
        if (supportedClasses.containsKey(type)) {
            return supportedClasses.get(type)
        }
        boolean supported = computeIsSupported(type)
        supportedClasses.put(type, supported)
        return supported
    }

    @Override
    final CachedClassMetadata getClassMetadata(Class type) {
        CachedClassMetadata metadata = classMetadata.get(type)
        if (metadata == null) {
            metadata = new CachedClassMetadata(createClassMetadata(type))
            classMetadata.put(type, metadata)
        }
        return metadata
    }

    @Override
    String getId(Object object) {
        return getId(object.getClass(), DefaultGroovyMethods.getProperties(object))
    }

    @Override
    String getId(Class type, Map<String, Object> properties) {
        ClassMetadata metadata = getClassMetadata(type)
        Object id = metadata.getId(properties)
        return id != null ? String.valueOf(id) : null
    }

    protected abstract ClassMetadata createClassMetadata(Class type)

    protected abstract boolean computeIsSupported(Class type)
}
