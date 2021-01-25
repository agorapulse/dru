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
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public interface PropertyMappingDefinition {

    <T> PropertyMappingDefinition to(Class<T> type);

    default <T> PropertyMappingDefinition to(Class<T> type,
                                     @DelegatesTo(type = "com.agorapulse.dru.TypeMappingDefinition<T>", strategy = Closure.DELEGATE_FIRST)
                                     @ClosureParams(value = FromString.class, options = "com.agorapulse.dru.TypeMappingDefinition<T>")
                                         Closure<TypeMappingDefinition<T>> configuration
    ) {
        return to(type, ConsumerWithDelegate.create(configuration));
    }

    <T> PropertyMappingDefinition to(Class<T> type, Consumer<TypeMappingDefinition<T>> configuration);

    <T> PropertyMappingDefinition to(Map<String, Class<T>> propertyAndType);

    default <T> PropertyMappingDefinition to(Map<String, Class<T>> propertyAndType,
                                     @DelegatesTo(type = "com.agorapulse.dru.TypeMappingDefinition<T>", strategy = Closure.DELEGATE_FIRST)
                                     @ClosureParams(value = FromString.class, options = "com.agorapulse.dru.TypeMappingDefinition<T>")
                                         Closure<TypeMappingDefinition<T>> configuration
    ) {
        return to(propertyAndType, ConsumerWithDelegate.create(configuration));
    }

    default <T> PropertyMappingDefinition to(String propertyName, Class<T> propertyType, Consumer<TypeMappingDefinition<T>> configuration) {
        return to(Collections.singletonMap(propertyName, propertyType), configuration);
    }

    <T> PropertyMappingDefinition to(Map<String, Class<T>> propertyAndType, Consumer<TypeMappingDefinition<T>> configuration);

    PropertyMappingDefinition ignore(Iterable<String> ignored);

    PropertyMappingDefinition ignore(String first, String... rest);

}
