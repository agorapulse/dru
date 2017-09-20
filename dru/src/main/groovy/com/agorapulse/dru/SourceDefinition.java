package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface SourceDefinition {
    SourceDefinition map(String path,
                         @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
                             Closure<PropertyMappingDefinition> configuration
    );

    SourceDefinition map(
        @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
            Closure<PropertyMappingDefinition> configuration
    );

}
