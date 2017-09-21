package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.InputStream;

class DefaultSource implements SourceDefinition, Source {

    DefaultSource(Object referenceObject, String path) {
        this.referenceObject = referenceObject;
        this.path = path;
        propertyMappings = new PropertyMappings(path);
    }

    @Override
    public SourceDefinition map(String path,
                                @DelegatesTo(value = com.agorapulse.dru.PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
                Closure<PropertyMappingDefinition> configuration
    ) {
        PropertyMapping mapping = propertyMappings.findOrCreate(path);
        DefaultGroovyMethods.with(mapping, configuration);
        return this;
    }

    @Override
    public SourceDefinition map(Closure<PropertyMappingDefinition> configuration) {
        return map("", configuration);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public InputStream getSourceStream() {
        Class reference = referenceObject instanceof Class ? (Class) referenceObject : referenceObject.getClass();
        return reference.getResourceAsStream(reference.getSimpleName() + "/" + path);
    }

    @Override
    public PropertyMappings getRootPropertyMappings() {
        return propertyMappings;
    }

    private final Object referenceObject;
    private final String path;
    private final PropertyMappings propertyMappings;
}
