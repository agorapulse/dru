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

import com.agorapulse.dru.parser.Parser
import com.agorapulse.dru.persistence.Client
import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.DefaultGroovyMethods

import java.util.function.Consumer

/**
 * Mapping of the source property to one or more type mappings.
 */
class PropertyMapping implements PropertyMappingDefinition {

    private final String parentPath
    private final String path
    private final TypeMappings typeMappings = new TypeMappings()
    private final Set<String> ignored = new LinkedHashSet<String>()

    PropertyMapping(String parentPath, String path) {
        this.parentPath = parentPath
        this.path = path
    }

    @Override
    <T> PropertyMappingDefinition to(
        Class<T> type,
        Consumer<TypeMappingDefinition<T>> configuration = { }
    ) {
        TypeMapping<T> mapping = typeMappings.findOrCreate(type, path)
        configuration.accept(mapping)
        mapping.ignore(ignored)
        return this
    }

    @Override
    <T> PropertyMappingDefinition to(
        Map<String, Class<T>> propertyAndType,
        Consumer<TypeMappingDefinition<T>> configuration = { }
    ) {
        if (!propertyAndType || propertyAndType.size() != 1) {
            throw new IllegalArgumentException("Excatly one 'propertyName: Type' pair expected. Got: $propertyAndType")
        }
        TypeMapping<T> mapping = typeMappings.findOrCreate(propertyAndType.values().first(), path)
        configuration.accept(mapping)
        mapping.name = propertyAndType.keySet().first()
        mapping.ignore(ignored)
        return this
    }

    PropertyMapping ignore(Iterable<String> ignored) {
        this.@ignored.addAll(ignored)
        typeMappings.each {
            it.ignore(ignored)
        }
        return this
    }

    PropertyMapping ignore(String first, String... rest) {
        ignored << first
        if (rest) {
            this.@ignored.addAll(rest)
        }
        typeMappings.each {
            it.ignore(first, rest)
        }
        return this
    }

    String getPath() {
        return path
    }

    String getFullPath() {
        return parentPath?.contains('/') ? "${parentPath}.$path" : "$parentPath/$path"
    }

    TypeMappings getTypeMappings() {
        return typeMappings
    }

    @Override
    String toString() {
        return "PropertyMapping[$fullPath]"
    }

    @CompileStatic
    @SuppressWarnings([
        'Instanceof',
        'CyclomaticComplexity',
        'ParameterReassignment',
        'AbcMetric',
        'MethodSize',
    ])
    // TODO: refactor this beast
    Object processPropertyValue(DataSet dataSet, DataSetMapping dataSetMapping, Parser parser, TypeMapping typeMappingToUse, Object property) {
        if (!(property instanceof Map)) {
            return processPropertyValueNoMap(dataSet, dataSetMapping, typeMappingToUse, property)
        }

        if (typeMappingToUse && (Map.isAssignableFrom(typeMappingToUse.type) || typeMappingToUse.type == Object)) {
            // no need to handle the map or if the mapping is to object
            return property
        }

        MockObject fixture = new MockObject(new LinkedHashMap<>(property as Map<String, Object>))

        typeMappingToUse = typeMappingToUse ?: typeMappings.find(fixture)

        Objects.requireNonNull(typeMappingToUse, "No type definition for $this and $fixture")

        Class type = typeMappingToUse.type

        Client client = findClient(dataSetMapping, type, typeMappingToUse, property)

        ClassMetadata classMetadata = Objects.requireNonNull(client.getClassMetadata(type), "No class metadata found for ${type}! $this, $typeMappingToUse: $fixture")

        dataSetMapping.applyOverrides(type, fixture, fixture.asImmutable())
        typeMappingToUse.overrides.apply(fixture, fixture.asImmutable())

        Map<String, Object> payload = [:]
        Map<String, List<Object>> pendingToMany = [:].withDefault { [] }
        List<DelayedAssignment> delayedResolutions = []
        Set<MissingProperty> missing = new LinkedHashSet<>()

        fixture.each {
            if (it.value == null) {
                return
            }

            String propertyName = it.key

            if (propertyName in typeMappingToUse.ignored || dataSetMapping.isIgnored(type, propertyName)) {
                return
            }

            PropertyMapping nestedMapping = getNestedMapping(propertyName, typeMappingToUse, dataSetMapping)

            TypeMapping nestedTypeMapping = nestedMapping.typeMappings.find(it.value)

            if (nestedTypeMapping?.name) {
                propertyName = nestedTypeMapping.name
            }

            PropertyMetadata persistentProperty = classMetadata.getPersistentProperty(propertyName)

            if (!persistentProperty || !persistentProperty.persistent) {
                if (nestedTypeMapping && (propertyName == 'new' || propertyName == '_')) {
                    if (it.value instanceof Map) {
                        delayedResolutions << new DelayedAssignment(
                            null,
                            nestedMapping,
                            nestedTypeMapping,
                            it.value
                        )
                        return
                    }
                    if (it.value instanceof Iterable) {
                        for (item in it.value) {
                            delayedResolutions << new DelayedAssignment(
                                null,
                                nestedMapping,
                                nestedTypeMapping,
                                item
                            )
                        }
                        return
                    }
                }
                missing << MissingProperty.create("${fullPath}.${propertyName}", propertyName, it.value, type)
                return
            }

            boolean shouldProceedWithNestedMapping = nestedTypeMapping && it.value != null

            if (!shouldProceedWithNestedMapping && !persistentProperty.association && !persistentProperty.embedded) {
                if (it.value == null || persistentProperty.type.isInstance(it.value)) {
                    payload.put(propertyName, it.value)
                    return
                }

                payload.put(propertyName, parser.convertValue("$fullPath.$it.key", it.value, persistentProperty.type))
                return
            }

            // this is safe to call as it adds the type to the end of the list if some mappings are already present
            // so anything already present is ignored
            nestedTypeMapping = nestedTypeMapping ?: typeMappingToUse
                .propertyMappings.findOrCreate(propertyName)
                .typeMappings.findOrCreate(persistentProperty.referencedPropertyType, propertyName)

            if (((persistentProperty.embedded || shouldProceedWithNestedMapping) && !persistentProperty.collectionType)
                || persistentProperty.isOneToOne() || persistentProperty.isManyToOne()
            ) {
                payload.put(propertyName, nestedMapping.processPropertyValue(dataSet, dataSetMapping, parser, nestedTypeMapping, it.value))
                return
            }

            if (it.value instanceof Iterable) {
                if (persistentProperty.owningSide) {
                    // do stuff with to many
                    pendingToMany.put(propertyName, it.value.collect { item ->
                        nestedMapping.processPropertyValue(dataSet, dataSetMapping, parser, nestedTypeMapping, item)
                    })
                    return
                }

                Objects.requireNonNull(persistentProperty.referencedPropertyName,
                    "Cannot determine referenced property name: $fullPath => $propertyName with value $it.value"
                )

                it.value.each { item ->
                    delayedResolutions << new DelayedAssignment(
                            persistentProperty.referencedPropertyName,
                            nestedMapping, nestedTypeMapping,
                            item
                    )
                }
                return
            }

            Objects.requireNonNull(persistentProperty.referencedPropertyName,
                "Cannot determine referenced property name: $fullPath => $propertyName with value $it.value"
            )

            delayedResolutions << new DelayedAssignment(
                    persistentProperty.referencedPropertyName,
                    nestedMapping, nestedTypeMapping,
                    it.value
            )
        }

        Object id = classMetadata.getId(fixture)

        Object newStuff
        if (id) {
            newStuff = dataSet.findByTypeAndOriginalId(type, id)
        }

        if (newStuff == null) {
            newStuff = client.newInstance(parser, type, payload)
        } else {
            assignProperties(newStuff, payload, classMetadata.idPropertyNames)
        }

        handleDefaults(dataSetMapping, typeMappingToUse, type, fixture, newStuff)

        client.save(newStuff)

        // some client such as GORM can obtain id after save
        dataSet.add(newStuff, id)

        pendingToMany.each { String associationName, Iterable values ->
            values.each { value ->
                client.addTo(newStuff, associationName, value)
            }
        }

        if (pendingToMany) {
            client.save(newStuff)
        }

        processDelayedResolutions(delayedResolutions, newStuff, dataSet, dataSetMapping, parser)

        removeAccessedKeysFromMissing(missing, fixture, dataSet)

        return typeMappingToUse.process(newStuff)
    }

    @SuppressWarnings('CouldBeElvis')
    private static void handleDefaults(DataSetMapping dataSetMapping, TypeMapping typeMappingToUse, Class type, MockObject fixture, Object newStuff) {
        MockObject defaults = new MockObject()
        dataSetMapping.applyDefaults(type, defaults, fixture.asImmutable())
        typeMappingToUse.defaults.apply(defaults, fixture.asImmutable())

        for (Map.Entry<String, Object> defaultValue in defaults.entrySet()) {
            if (!newStuff."$defaultValue.key") {
                newStuff."$defaultValue.key" = defaultValue.value
            }
        }
    }

    @CompileStatic
    @SuppressWarnings('UnnecessaryGetter')
    private static void removeAccessedKeysFromMissing(Set<MissingProperty> missing, MockObject fixture, DataSet dataSet) {
        for (MissingProperty ignoredValue in missing) {
            if (ignoredValue.propertyName in fixture.getAccessedKeys()) {
                continue
            }
            dataSet.report.add(ignoredValue)
        }
    }

    @CompileStatic
    private static void processDelayedResolutions(
        List<DelayedAssignment> delayedResolutions,
        Object newStuff,
        DataSet dataSet,
        DataSetMapping dataSetMapping,
        Parser parser
    ) {
        for (DelayedAssignment delayedResolution in delayedResolutions) {
            if (delayedResolution.propertyName) {
                delayedResolution.payload[delayedResolution.propertyName] = newStuff
            }
            delayedResolution.mapping.processPropertyValue(dataSet, dataSetMapping, parser, delayedResolution.typeMapping, delayedResolution.payload)
        }
    }

    @CompileStatic
    @SuppressWarnings(['Instanceof'])
    Object processPropertyValueNoMap(DataSet dataSet, DataSetMapping dataSetMapping, TypeMapping typeMappingToUse, Object property) {
        if (typeMappingToUse && typeMappingToUse.type.isInstance(property)) {
            return property
        }
        if (property instanceof String && typeMappingToUse && Enum.isAssignableFrom(typeMappingToUse.type)) {
            return typeMappingToUse.type.getMethod('valueOf', String).invoke(null, property)
        }

        if (typeMappingToUse && (property instanceof Number || property instanceof CharSequence)) {
            Object byId = dataSet.findByTypeAndOriginalId(typeMappingToUse.type, property)

            if (byId) {
                // this is ID of something we've already created
                Class type = typeMappingToUse.type

                Client client = findClient(dataSetMapping, type, typeMappingToUse, property)

                ClassMetadata classMetadata = client.getClassMetadata(type)

                return classMetadata.getId(DefaultGroovyMethods.getProperties(byId))
            }

            throw new IllegalStateException("Value $property with path $fullPath is mapped to id of $typeMappingToUse.type" +
                ' but it is not loaded yet. Please load the entity before loading this source.')
        }

        throw new IllegalArgumentException("Property is not a Map either already processed entity at $fullPath: $property")
    }

    @CompileStatic
    private static Client findClient(DataSetMapping dataSetMapping, Class type, TypeMapping typeMappingToUse, Object property) {
        Objects.requireNonNull(dataSetMapping.clients.find { it.isSupported(type) }, "No client supports $type! $this, $typeMappingToUse: $property")
    }

    @CompileStatic
    private static PropertyMapping getNestedMapping(String propertyName, TypeMapping typeMappingToUse, DataSetMapping dataSetMapping) {
        PropertyMapping nestedMapping = typeMappingToUse.propertyMappings.find(propertyName)

        if (nestedMapping) {
            return nestedMapping
        }

        TypeMapping shared = dataSetMapping.typeMappings.findByType(typeMappingToUse.type)
        if (shared) {
            nestedMapping = shared.propertyMappings.find(propertyName)
        }

        if (nestedMapping) {
            return nestedMapping
        }

        return typeMappingToUse.propertyMappings.findOrCreate(propertyName)
    }

    private static void assignProperties(Object newStuff, Map<String, Object> payload, Set<String> idNames) {
        payload.each {
            if (it.key in idNames) {
                return
            }
            try {
                newStuff."$it.key" = it.value
            } catch (ReadOnlyPropertyException ignored) {
                // read only properties can be ignored - they are either derived or set in the constructor
            }
        }
    }
}
