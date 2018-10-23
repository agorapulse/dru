package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

abstract class AbstractSource implements SourceDefinition, Source {

    AbstractSource(Object referenceObject, String path) {
        this.referenceObject = referenceObject;
        this.path = path;
        this.propertyMappings = new PropertyMappings(path);
    }

    @Override
    public final SourceDefinition map(String path,
                                @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
                Closure<PropertyMappingDefinition> configuration
    ) {
        PropertyMapping mapping = propertyMappings.findOrCreate(path);
        DefaultGroovyMethods.with(mapping, configuration);
        return this;
    }

    @Override
    public final String getPath() {
        return path;
    }

    @Override
    public final SourceDefinition map(Closure<PropertyMappingDefinition> configuration) {
        return map("", configuration);
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
