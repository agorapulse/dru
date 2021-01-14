package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import groovy.transform.stc.SimpleType;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public interface DataSetMappingDefinition {

    default DataSetMappingDefinition from(
        String relativePath,
        @DelegatesTo(value = SourceDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.dru.SourceDefinition")
            Closure<SourceDefinition> configuration
    ) {
        return from(relativePath, ConsumerWithDelegate.create(configuration));
    }

    DataSetMappingDefinition from(String relativePath, Consumer<SourceDefinition> configuration);

    default DataSetMappingDefinition from(
        File file,
        @DelegatesTo(value = SourceDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.dru.SourceDefinition")
            Closure<SourceDefinition> configuration
    ) throws IOException {
        return from(file, ConsumerWithDelegate.create(configuration));
    }

    DataSetMappingDefinition from(File file, Consumer<SourceDefinition> configuration) throws IOException;

    default <T> DataSetMappingDefinition any(
        Class<T> type,
        @DelegatesTo(type = "com.agorapulse.dru.TypeMappingDefinition<T>", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.dru.TypeMappingDefinition<T>")
            Closure<TypeMappingDefinition<T>> configuration
    ) {
        return any(type, ConsumerWithDelegate.create(configuration));
    }

    <T> DataSetMappingDefinition any(
        Class<T> type,
        Consumer<TypeMappingDefinition<T>> configuration
    );

    DataSetMappingDefinition include(PreparedDataSet plan);

    DataSetMappingDefinition whenLoaded(WhenLoaded listener);

    DataSetMappingDefinition onChange(OnChange listener);

    interface OnChange {
        void doOnChange(DataSet dataSet);
    }

    interface WhenLoaded {
        void doWhenLoaded(DataSet dataSet);
    }

}
