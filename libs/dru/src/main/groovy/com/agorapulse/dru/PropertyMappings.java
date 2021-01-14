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
