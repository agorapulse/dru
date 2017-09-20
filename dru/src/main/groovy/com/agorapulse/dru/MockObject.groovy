package com.agorapulse.dru

import groovy.transform.PackageScope

@PackageScope class MockObject {

    @Delegate private Map<String, Object> delegate

    private Set<String> accessedKeys = new LinkedHashSet<>()

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
