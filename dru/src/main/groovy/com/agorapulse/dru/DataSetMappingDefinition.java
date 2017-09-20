package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;

public interface DataSetMappingDefinition {

    DataSetMappingDefinition from(String relativePath, @DelegatesTo(value = SourceDefinition.class, strategy = Closure.DELEGATE_FIRST) Closure<SourceDefinition> configuration);

    <T> DataSetMappingDefinition any(Class<T> type);

    <T> DataSetMappingDefinition any(Class<T> type,
                                     @DelegatesTo(type = "com.agorapulse.dru.TypeMappingDefinition<T>", strategy = Closure.DELEGATE_FIRST)
                                     @ClosureParams(value = FromString.class, options = "com.agorapulse.dru.TypeMappingDefinition<T>")
                                         Closure<TypeMappingDefinition<T>> configuration
    );

    DataSetMappingDefinition include(PreparedDataSet plan);

    DataSetMappingDefinition whenLoaded(WhenLoaded listener);

    interface WhenLoaded {
        void doWhenLoaded(DataSet dataSet);
    }
}
