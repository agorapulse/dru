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
import space.jasan.support.groovy.closure.BiConsumerWithDelegate;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;
import space.jasan.support.groovy.closure.FunctionWithDelegate;
import space.jasan.support.groovy.closure.PredicateWithDelegate;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface TypeMappingDefinition<T> {

    default TypeMappingDefinition<T> when(
        @DelegatesTo(type = "java.util.Map<String,Object>", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "java.util.Map<String, Object>")
            Closure<Boolean> condition
    ) {
        return when(PredicateWithDelegate.create(condition));
    }

    TypeMappingDefinition<T> when(Predicate<Map<String, Object>> condition);

    default TypeMappingDefinition<T> and(
        @DelegatesTo(type = "java.util.Map<String,Object>", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "java.util.Map<String, Object>")
            Closure<Boolean> condition
    ) {
        return and(PredicateWithDelegate.create(condition));
    }

    default TypeMappingDefinition<T> and(Predicate<Map<String, Object>> condition) {
        return when(condition);
    }

    default TypeMappingDefinition<T> defaults(
        @DelegatesTo(type = "T", strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString.class, options = "java.util.Map<String, Object>")
            Closure defaultsSetter
    ) {
        return defaults(BiConsumerWithDelegate.create(defaultsSetter, Closure.DELEGATE_ONLY));
    }

    TypeMappingDefinition<T> defaults(BiConsumer<Map<String, Object>, Map<String, Object>> defaultsSetter);

    default TypeMappingDefinition<T> overrides(
        @DelegatesTo(type = "T", strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString.class, options = "java.util.Map<String, Object>")
            Closure defaultsSetter
    ) {
        return overrides(BiConsumerWithDelegate.create(defaultsSetter, Closure.DELEGATE_ONLY));
    }

    TypeMappingDefinition<T> overrides(BiConsumer<Map<String, Object>, Map<String, Object>> defaultsSetter);

    default TypeMappingDefinition<T> just(
        @DelegatesTo(type = "T", strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString.class, options = "T")
            Closure<Object> query
    ) {
        return just(FunctionWithDelegate.create(query, Closure.DELEGATE_ONLY));
    }

    TypeMappingDefinition<T> just(Function<T, Object> query);

    TypeMappingDefinition<T> ignore(Iterable<String> ignored);

    TypeMappingDefinition<T> ignore(String first, String... rest);

    default TypeMappingDefinition<T> ignore(
        @DelegatesTo(type = "T", strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString.class, options = "T")
            Closure ignoreConfigurer
    ) {
        MockObject map = new MockObject();
        ConsumerWithDelegate.create(ignoreConfigurer, Closure.DELEGATE_ONLY).accept(map);
        ignore(map.getAccessedKeys());
        return this;
    }

    PropertyMapping map(String path);

    default TypeMappingDefinition<T> map(
        String path,
        @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.dru.PropertyMappingDefinition")
            Closure<PropertyMappingDefinition> configuration
    ) {
        return map(path, ConsumerWithDelegate.create(configuration));
    }

    TypeMappingDefinition<T> map(String path, Consumer<PropertyMappingDefinition> configuration);

    default TypeMappingDefinition<T> map(
        Iterable<String> paths,
        @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.dru.PropertyMappingDefinition")
            Closure<PropertyMappingDefinition> configuration
    ) {
        return map(paths, ConsumerWithDelegate.create(configuration));
    }

    default TypeMappingDefinition<T> map(Iterable<String> paths, Consumer<PropertyMappingDefinition> configuration) {
        for (String path : paths) {
            map(path, configuration);
        }
        return this;
    }

}
