package com.agorapulse.dru.mn.data.jdbc.meta;

import com.agorapulse.dru.persistence.meta.ClassMetadata;
import com.agorapulse.dru.persistence.meta.PropertyMetadata;
import io.micronaut.data.model.runtime.RuntimePersistentEntity;
import io.micronaut.data.model.runtime.RuntimePersistentProperty;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
public class MicronautDataClassMetadata implements ClassMetadata {

    private final RuntimePersistentEntity entity;
    private final Class type;

    public MicronautDataClassMetadata(RuntimePersistentEntity entity, Class type) {
        this.entity = entity;
        this.type = type;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<PropertyMetadata> getPersistentProperties() {
        Stream<RuntimePersistentProperty> stream = entity.getPersistentProperties().stream();
        return stream.map(MicronautDataPropertyMetadata::new).collect(Collectors.toList());
    }

    @Override
    public PropertyMetadata getPersistentProperty(String name) {
        RuntimePersistentProperty property = entity.getPropertyByName(name);

        if (property == null) {
            return null;
        }

        return new MicronautDataPropertyMetadata(property);
    }

    @Override
    public Object getId(Map<String, Object> fixture) {
        return getIdPropertyNames().stream().findFirst().map(fixture::get).orElse(null);
    }

    @Override
    public Set<String> getIdPropertyNames() {
        RuntimePersistentProperty identity = entity.getIdentity();

        if (identity == null) {
            return Collections.emptySet();
        }

        return Collections.singleton(identity.getName());
    }
}
