package com.agorapulse.dru.persistence.meta;

import com.google.common.collect.ImmutableSet;

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
        this.idPropertyNames = ImmutableSet.copyOf(original.getIdPropertyNames());
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
        return idPropertyNames;
    }

    public ClassMetadata getOriginal() {
        return original;
    }
}
