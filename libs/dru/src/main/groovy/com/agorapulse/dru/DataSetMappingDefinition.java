/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    default DataSetMappingDefinition from(String relativePath) {
        return from(relativePath, s -> {});
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

    default DataSetMappingDefinition from(File file) throws IOException {
        return from(file, s -> {});
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

    default DataSetMappingDefinition whenLoaded(
        @DelegatesTo(type = "com.agorapulse.dru.DataSet", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.dru.DataSet")
        Closure<?> listener
    ) {
        return whenLoaded(dataSet -> ConsumerWithDelegate.create(listener).accept(dataSet));
    }

    DataSetMappingDefinition onChange(OnChange listener);

    default DataSetMappingDefinition onChange(
        @DelegatesTo(type = "com.agorapulse.dru.DataSet", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.dru.DataSet")
            Closure<?> listener
    ) {
        return onChange(dataSet -> ConsumerWithDelegate.create(listener).accept(dataSet));
    }

    interface OnChange {
        void doOnChange(DataSet dataSet);
    }

    interface WhenLoaded {
        void doWhenLoaded(DataSet dataSet);
    }

}
