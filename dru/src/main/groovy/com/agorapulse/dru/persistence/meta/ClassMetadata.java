package com.agorapulse.dru.persistence.meta;

import java.util.Map;

/**
 * Minimal class metadata derived from GrailsDomainClass.
 */
public interface ClassMetadata {

    Class getType();
    Iterable<PropertyMetadata> getPersistentProperties();
    PropertyMetadata getPersistentProperty(String name);

    Object getId(Map<String, Object> fixture);
}
