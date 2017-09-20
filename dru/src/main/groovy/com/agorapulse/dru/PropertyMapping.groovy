package com.agorapulse.dru

import com.agorapulse.dru.parser.Parser
import com.agorapulse.dru.persistence.Client
import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import groovy.transform.CompileStatic
import groovy.transform.ThreadInterrupt
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

class PropertyMapping implements PropertyMappingDefinition {

    private final String parentPath
    private final String path
    private final TypeMappings typeMappings = new TypeMappings()
    private final Set<String> ignored = new LinkedHashSet<String>()

    PropertyMapping(String parentPath, String path) {
        this.parentPath = parentPath
        this.path = path
    }

    public <T> PropertyMappingDefinition to(Class<T> type, @DelegatesTo(type = 'com.agorapulse.dru.TypeMappingDefinition<T>', strategy = Closure.DELEGATE_FIRST) @ClosureParams(value = FromString, options = 'com.agorapulse.dru.TypeMappingDefinition<T>') Closure<TypeMappingDefinition<T>> configuration = Closure.IDENTITY) {
        TypeMapping<T> mapping = typeMappings.findOrCreate(type, path)
        mapping.with(configuration)
        mapping.ignore(ignored)
        return this
    }

    public <T> PropertyMappingDefinition to(Map<String, Class<T>> propertyAndType, @DelegatesTo(type = 'com.agorapulse.dru.TypeMappingDefinition<T>', strategy = Closure.DELEGATE_FIRST) @ClosureParams(value = FromString, options = 'com.agorapulse.dru.TypeMappingDefinition<T>') Closure<TypeMappingDefinition<T>> configuration = Closure.IDENTITY) {
        if (!propertyAndType || propertyAndType.size() != 1) {
            throw new IllegalArgumentException("Excatly one 'propertyName: Type' pair expected. Got: $propertyAndType")
        }
        TypeMapping<T> mapping = typeMappings.findOrCreate(propertyAndType.values().first(), path)
        mapping.with(configuration)
        mapping.name = propertyAndType.keySet().first()
        mapping.ignore(ignored)
        return this
    }

    public PropertyMapping ignore(Iterable<String> ignored) {
        this.@ignored.addAll(ignored)
        typeMappings.each {
            it.ignore(ignored)
        }
        return this
    }

    public PropertyMapping ignore(String first, String... rest) {
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
        if (parentPath?.contains('/')) {
            return "${parentPath}.$path"
        }
        return "$parentPath/$path"
    }

    TypeMappings getTypeMappings() {
        return typeMappings
    }

    @Override
    String toString() {
        return "PropertyMapping $fullPath"
    }



    @CompileStatic
    @ThreadInterrupt
    Object processPropertyValue(DataSet dataSet, DataSetMapping dataSetMapping, Parser parser, TypeMapping typeMappingToUse, property) {
        if (!(property instanceof Map)) {
            if (property instanceof String && typeMappingToUse && Enum.isAssignableFrom(typeMappingToUse.type)) {
                return typeMappingToUse.type.getMethod('valueOf', String).invoke(null, property)
            }
            if (dataSet.findAllByType(property.getClass()).contains(property)) {
                return property
            }

            if (typeMappingToUse && (property instanceof Number || property instanceof CharSequence)) {
                Object byId = dataSet.findByTypeAndOriginalId(typeMappingToUse.type, property)

                if (byId) {
                    // this is ID of something we've already created
                    Class type = typeMappingToUse.type

                    Client client = dataSetMapping.clients.find { it.isSupported(type) }

                    if (!client) {
                        throw new IllegalArgumentException(("No client supports $type! $this, $typeMappingToUse: $property"))
                    }

                    ClassMetadata classMetadata = client.getClassMetadata(type)
                    return classMetadata.getId(byId.properties)
                }

                // this is ID of something we don't care about so keep it as it is
                return property
            }


            throw new IllegalArgumentException("Property is not a Map either already processed entity at $fullPath: $property")
        }

        if (typeMappingToUse && (Map.isAssignableFrom(typeMappingToUse.type) || typeMappingToUse.type == Object)) {
            // no need to handle the map or if the mapping is to object
            return property
        }

        MockObject fixture = new MockObject(new LinkedHashMap<>(property as Map<String, Object>))

        if (!typeMappingToUse) {
            typeMappingToUse = typeMappings.find(fixture)
        }

        if (!typeMappingToUse) {
            throw new IllegalStateException("No type definition for $this and $fixture")
        }

        Class type = typeMappingToUse.type

        Client client = dataSetMapping.clients.find { it.isSupported(type) }

        if (!client) {
            throw new IllegalArgumentException(("No client supports $type! $this, $typeMappingToUse: $fixture"))
        }

        ClassMetadata classMetadata = client.getClassMetadata(type)

        if (!classMetadata) {
            throw new IllegalStateException("No class metadata found for ${type}! $this, $typeMappingToUse: $fixture")
        }

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

            PropertyMapping nestedMapping = typeMappingToUse.propertyMappings.find(propertyName)

            if (!nestedMapping) {
                TypeMapping shared = dataSetMapping.typeMappings.findByType(typeMappingToUse.type)
                if (shared) {
                    nestedMapping = shared.propertyMappings.find(propertyName)
                }
            }

            if (!nestedMapping) {
                nestedMapping = typeMappingToUse.propertyMappings.findOrCreate(propertyName)
            }

            TypeMapping nestedTypeMapping = nestedMapping.typeMappings.find(it.value)

            if (nestedTypeMapping?.name) {
                propertyName = nestedTypeMapping.name
            }

            PropertyMetadata persistentProperty = classMetadata.getPersistentProperty(propertyName)

            if (!persistentProperty || !persistentProperty.persistent) {
                if (nestedTypeMapping && (propertyName == 'new' || propertyName == '_')) {
                    if (it.value instanceof Map) {
                        nestedMapping.processPropertyValue(dataSet, dataSetMapping, parser, nestedTypeMapping, it.value)
                        return
                    }
                    if (it.value instanceof Iterable) {
                        for (item in it.value) {
                            nestedMapping.processPropertyValue(dataSet, dataSetMapping, parser, nestedTypeMapping, item)
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
            if (!nestedTypeMapping) {
                typeMappingToUse.map(propertyName) {
                    to persistentProperty.referencedPropertyType
                }
                nestedTypeMapping = typeMappingToUse.propertyMappings.findOrCreate(propertyName).typeMappings.find(it.value)
            }

            if (((persistentProperty.embedded || shouldProceedWithNestedMapping) && !persistentProperty.collectionType) || persistentProperty.isOneToOne() || persistentProperty.isManyToOne()) {
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
                it.value.each { item ->
                    delayedResolutions << new DelayedAssignment(
                            persistentProperty.referencedPropertyName,
                            nestedMapping, nestedTypeMapping,
                            item
                    )
                }
                return
            }

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
            if (id) {
                dataSet.add(type, id, newStuff)
            }
        } else {
            assignProperties(newStuff, payload)
        }

        dataSetMapping.applyDefaults(type, newStuff, fixture.asImmutable())
        typeMappingToUse.defaults.apply(newStuff, fixture.asImmutable())

        client.save(newStuff)

        pendingToMany.each {
            client.addTo(newStuff, it.key, it.value)
        }

        if (pendingToMany) {
            client.save(newStuff)
        }

        for (DelayedAssignment delayedResolution in delayedResolutions) {
            delayedResolution.payload[delayedResolution.propertyName] = newStuff
            delayedResolution.mapping.processPropertyValue(dataSet, dataSetMapping, parser, delayedResolution.typeMapping, delayedResolution.payload)
        }

        for (MissingProperty ignoredValue in missing) {
            if (ignoredValue.propertyName in fixture.getAccessedKeys()) {
                continue
            }
            dataSet.report.add(ignoredValue)
        }


        return typeMappingToUse.process(newStuff)
    }

    private static void assignProperties(newStuff, Map<String, Object> payload) {
        payload.each {
            newStuff."$it.key" = it.value
        }
    }
}
