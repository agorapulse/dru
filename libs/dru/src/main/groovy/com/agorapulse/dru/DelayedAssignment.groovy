package com.agorapulse.dru

import groovy.transform.PackageScope

/**
 * Collects information needed for executing property assignment later.
 */
@PackageScope class DelayedAssignment {

    final String propertyName
    final PropertyMapping mapping
    final TypeMapping typeMapping
    final Object payload

    DelayedAssignment(String propertyName, PropertyMapping mapping, TypeMapping typeMapping, Object payload) {
        this.propertyName = propertyName
        this.typeMapping = typeMapping
        this.mapping = mapping
        this.payload = payload
    }
}
