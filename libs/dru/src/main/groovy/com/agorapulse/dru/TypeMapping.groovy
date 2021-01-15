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
package com.agorapulse.dru

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate

/**
 * Mapping from source to specified object of given type.
 * @param <T> type of the destination object
 */
@CompileStatic
class TypeMapping<T> implements TypeMappingDefinition<T> {

    private final String path
    private final Class<T> type
    private final List<Predicate> conditions = []
    private final Set<String> ignored = new LinkedHashSet<String>()

    // sets the value directly to the newly created instance if the value is falsy
    private final Customisations defaultsCustomisation = new Customisations()

    // overrides the value in the incoming map, e.g. type tweaks
    private final Customisations overridesCustomisation = new Customisations()

    private final PropertyMappings propertyMappings

    private Function query = Function.identity()

    @PackageScope String name

    TypeMapping(String path, Class<T> type) {
        this.path = path
        this.type = type
        this.propertyMappings = new PropertyMappings(path)
    }

    TypeMapping<T> when(Predicate condition) {
        conditions << condition
        return this
    }

    TypeMapping<T> defaults(BiConsumer<Map<String, Object>, Map<String, Object>> defaultsSetter) {
        defaultsCustomisation.add defaultsSetter
        return this
    }

    TypeMapping<T> overrides(BiConsumer<Map<String, Object>, Map<String, Object>> defaultsSetter) {
        overridesCustomisation.add defaultsSetter
        return this
    }

    @Override
    TypeMappingDefinition<T> just(Function<T, Object> query) {
        this.query = query
        return this
    }

    TypeMapping<T> ignore(Iterable<String> ignored) {
        this.@ignored.addAll(ignored)
        return this
    }

    TypeMapping<T> ignore(String first, String... rest) {
        ignored << first
        if (rest) {
            this.@ignored.addAll(rest)
        }
        return this
    }

    PropertyMapping map(String path) {
        return propertyMappings.findOrCreate(path)
    }

    TypeMapping<T> map(String path, Consumer<PropertyMappingDefinition> configuration) {
        PropertyMapping mapping = propertyMappings.findOrCreate(path)
        configuration.accept(mapping)
        return this
    }

    Class<T> getType() {
        return type
    }

    String getName() {
        return name
    }

    List<Predicate> getConditions() {
        return conditions.asImmutable()
    }

    Customisations getDefaults() {
        return defaultsCustomisation
    }

    Customisations getOverrides() {
        return overridesCustomisation
    }

    Set<String> getIgnored() {
        return ignored.asImmutable()
    }

    PropertyMappings getPropertyMappings() {
        return propertyMappings
    }

    Object process(T result) {
        query.apply(result)
    }

    @Override
    String toString() {
        return "TypeMapping[$type.simpleName]"
    }
}
