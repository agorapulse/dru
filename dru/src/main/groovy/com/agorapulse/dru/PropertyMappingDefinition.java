package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;

import java.util.Map;

public interface PropertyMappingDefinition {

    <T> PropertyMappingDefinition to(Class<T> type);
    <T> PropertyMappingDefinition to(Class<T> type,
                                     @DelegatesTo(type = "com.agorapulse.dru.TypeMappingDefinition<T>", strategy = Closure.DELEGATE_FIRST)
                                     @ClosureParams(value = FromString.class, options = "com.agorapulse.dru.TypeMappingDefinition<T>")
                                         Closure<TypeMappingDefinition<T>> configuration
    );

    <T> PropertyMappingDefinition to(Map<String, Class<T>> propertyAndType);
    <T> PropertyMappingDefinition to(Map<String, Class<T>> propertyAndType,
                                     @DelegatesTo(type = "com.agorapulse.dru.TypeMappingDefinition<T>", strategy = Closure.DELEGATE_FIRST)
                                     @ClosureParams(value = FromString.class, options = "com.agorapulse.dru.TypeMappingDefinition<T>")
                                         Closure<TypeMappingDefinition<T>> configuration
    );

    PropertyMappingDefinition ignore(Iterable<String> ignored);

    PropertyMappingDefinition ignore(String first, String... rest);

}
