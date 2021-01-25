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
package com.agorapulse.dru

import groovy.transform.PackageScope

/**
 * Mock object using Groovy dynamic capabilities to act as real object.
 * Instead of setting properties it collect inputs into a map. It registers which properties has been accessed
 * which can be useful to determine which properties were ignored during the import.
 */
@PackageScope class MockObject {

    @Delegate private final Map<String, Object> delegate

    private final Set<String> accessedKeys

    MockObject() {
        this([:])
    }

    MockObject(Map<String, Object> delegate) {
        this(delegate, new LinkedHashSet<String>())
    }

    private MockObject(Map<String, Object> delegate, Set<String> accessedKeys) {
        this.delegate = delegate
        this.accessedKeys = accessedKeys
    }

    Object get(Object key) {
        accessedKeys << key?.toString()
        delegate.get(key)
    }

    Set<String> getAccessedKeys() {
        return accessedKeys.asImmutable()
    }

    @Override
    void setProperty(String propertyName, Object newValue) {
        delegate.put(propertyName, newValue)
    }

    /**
     * @return an immutable mock object which won't change the delegated map but it still shares
     * the accessed keys set
     */
    MockObject asImmutable() {
        return new MockObject(delegate.asImmutable(), accessedKeys)
    }
}
