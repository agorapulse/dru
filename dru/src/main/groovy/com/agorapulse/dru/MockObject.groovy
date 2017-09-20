package com.agorapulse.dru

import groovy.transform.PackageScope

/**
 * Mock object using Groovy dynamic capabilities to act as real object.
 * Instead of setting properties it collect inputs into a map. It registers which properties has been accessed
 * which can be useful to determine which properties were ignored during the import.
 */
@PackageScope class MockObject {

    @Delegate private final Map<String, Object> delegate

    private final Set<String> accessedKeys = new LinkedHashSet<>()

    MockObject(Map<String, Object> delegate) {
        this.delegate = delegate
    }

    MockObject() {
        this.delegate = [:]
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
}
