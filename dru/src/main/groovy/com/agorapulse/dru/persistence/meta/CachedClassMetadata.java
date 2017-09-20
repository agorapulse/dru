package com.agorapulse.dru.persistence.meta;

import java.util.LinkedHashMap;
import java.util.Map;

public class CachedClassMetadata implements ClassMetadata {

    private final Class type;
    private final Map<String, PropertyMetadata> persistentProperties;
    private final ClassMetadata original;

    public CachedClassMetadata(ClassMetadata original) {
        this.type = original.getType();
        this.persistentProperties = new LinkedHashMap<>();
        for (PropertyMetadata metadata : original.getPersistentProperties()) {
            this.persistentProperties.put(metadata.getName(), metadata);
        }
        this.original = original;
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
        return persistentProperties.get(name);
    }

    @Override
    public Object getId(Map<String, Object> fixture) {
        return original.getId(fixture);
    }

    public ClassMetadata getOriginal() {
        return original;
    }
}
