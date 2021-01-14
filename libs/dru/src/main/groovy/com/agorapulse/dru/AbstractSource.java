package com.agorapulse.dru;

import java.util.function.Consumer;

abstract class AbstractSource implements SourceDefinition, Source {

    AbstractSource(Object referenceObject, String path) {
        this.referenceObject = referenceObject;
        this.path = path;
        this.propertyMappings = new PropertyMappings(path);
    }

    @Override
    public final SourceDefinition map(String path, Consumer<PropertyMappingDefinition> configuration
    ) {
        PropertyMapping mapping = propertyMappings.findOrCreate(path);
        configuration.accept(mapping);
        return this;
    }

    @Override
    public final String getPath() {
        return path;
    }

    public final Object getReferenceObject() {
        return referenceObject;
    }

    @Override
    public final PropertyMappings getRootPropertyMappings() {
        return propertyMappings;
    }

    private final Object referenceObject;
    private final String path;
    private final PropertyMappings propertyMappings;
}
