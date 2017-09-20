package com.agorapulse.dru;

import groovy.lang.Closure;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

final class TypeMappings implements Iterable<TypeMapping> {

    @Override
    public Iterator<TypeMapping> iterator() {
        return mappings.values().iterator();
    }

    public TypeMapping findByType(Class type) {
        return mappings.get(type);
    }

    @SuppressWarnings("unchecked")
    public TypeMapping find(Object fixture) {
        // type mapping works only for maps at the moment
        if (!(fixture instanceof Map)) {
            return find(Collections.singletonMap("value", fixture));
        }

        for (TypeMapping typeMapping : mappings.values()) {
            if (!typeMapping.getConditions().isEmpty()) {
                for (Closure<Boolean> condition : (Iterable<Closure<Boolean>>) typeMapping.getConditions()) {
                    if (condition.call(fixture)) {
                        return typeMapping;
                    }
                }
            } else {
                return typeMapping;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void applyOverrides(Class type, Object destination, Object source) {
        TypeMapping mapping = mappings.get(type);

        if (mapping == null) {
            return;
        }

        mapping.getOverrides().apply(destination, source);

        if (type.getSuperclass() != null) {
            applyOverrides(type.getSuperclass(), destination, source);
        }

        for (Class it : type.getInterfaces()) {
            applyOverrides(it, destination, source);
        }

    }

    @SuppressWarnings("unchecked")
    public <T> TypeMapping<T> findOrCreate(Class<T> type, String path) {
        TypeMapping<T> mapping = mappings.get(type);
        if (mapping == null) {
            TypeMapping<T> newMapping = new TypeMapping<>(path, type);
            mappings.put(type, newMapping);
            return newMapping;
        }
        return mapping;
    }

    @SuppressWarnings("unchecked")
    public void applyDefaults(Class<?> type, Object destination, Object source) {
        TypeMapping mapping = mappings.get(type);

        if (mapping == null) {
            return;
        }

        mapping.getDefaults().apply(destination, source);

        if (type.getSuperclass() != null) {
            applyDefaults(type.getSuperclass(), destination, source);
        }

        for (Class it : type.getInterfaces()) {
            applyDefaults(it, destination, source);
        }

    }

    @SuppressWarnings("unchecked")
    public boolean isIgnored(Class<?> type, String propertyName) {
        TypeMapping mapping = mappings.get(type);

        if (mapping == null) {
            return false;
        }

        if (mapping.getIgnored().contains(propertyName)) {
            return true;
        }

        if (type.getSuperclass() != null && isIgnored(type.getSuperclass(), propertyName)) {
            return true;
        }

        for (Class i : type.getInterfaces()) {
            if (isIgnored(i, propertyName)) {
                return true;
            }
        }

        return false;
    }

    private final Map<Class, TypeMapping> mappings = new LinkedHashMap<>();
}
