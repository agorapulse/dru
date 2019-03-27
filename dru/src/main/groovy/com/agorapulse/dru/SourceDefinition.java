package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.util.function.Consumer;

public interface SourceDefinition {
    default SourceDefinition map(
        String path,
        @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.dru.PropertyMappingDefinition")
            Closure<PropertyMappingDefinition> configuration
    ) {
        return map(path, ConsumerWithDelegate.create(configuration));
    }

    default SourceDefinition map(
        @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.dru.PropertyMappingDefinition")
            Closure<PropertyMappingDefinition> configuration
    ) {
        return map(ConsumerWithDelegate.create(configuration));
    }

    default SourceDefinition map(Consumer<PropertyMappingDefinition> configuration) {
        return map("", configuration);
    }

    SourceDefinition map(String path, Consumer<PropertyMappingDefinition> configuration);
}
