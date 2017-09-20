package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;

public interface TypeMappingDefinition<T> {

    TypeMappingDefinition<T> when(
        @DelegatesTo(type = "java.util.Map<String,Object>", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "java.util.Map<String, Object>")
            Closure<Boolean> condition
    );

    TypeMappingDefinition<T> and(
        @DelegatesTo(type = "java.util.Map<String,Object>", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "java.util.Map<String, Object>")
            Closure<Boolean> condition
    );

    TypeMappingDefinition<T> defaults(
        @DelegatesTo(type = "T", strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString.class, options = "java.util.Map<String, Object>")
            Closure defaultsSetter
    );

    TypeMappingDefinition<T> overrides(
        @DelegatesTo(type = "T", strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString.class, options = "java.util.Map<String, Object>")
            Closure defaultsSetter
    );

    TypeMappingDefinition<T> just(
        @DelegatesTo(type = "T", strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString.class, options = "T")
            Closure query
    );

    TypeMappingDefinition<T> ignore(Iterable<String> ignored);

    TypeMappingDefinition<T> ignore(String first, String... rest);

    TypeMappingDefinition<T> ignore(
        @DelegatesTo(type = "T", strategy = Closure.DELEGATE_ONLY)
            Closure ignoreConfigurer
    );

    PropertyMapping map(String path);

    TypeMappingDefinition<T> map(String path,
                                 @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
                                     Closure<PropertyMappingDefinition> configuration
    );

    TypeMappingDefinition<T> map(Iterable<String> paths,
                             @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
                             Closure<PropertyMappingDefinition> configuration
    );

}
