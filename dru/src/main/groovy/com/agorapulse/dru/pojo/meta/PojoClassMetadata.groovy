package com.agorapulse.dru.pojo.meta

import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import com.google.common.collect.ImmutableSet

import java.lang.annotation.Annotation
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Describes POJO class.
 */
class PojoClassMetadata implements ClassMetadata {

    final Class type
    private Map<String, PropertyMetadata> persistentProperties

    PojoClassMetadata(Class type) {
        this.type = type
    }

    @SuppressWarnings('Instanceof')
    static Class getPropertyType(Class type, String propertyName) {
        Type genericType = getGenericPropertyType(type, propertyName)
        if (genericType instanceof ParameterizedType) {
            return genericType.rawType as Class
        }
        return genericType as Class
    }

    static Type getGenericPropertyType(Class type, String propertyName) {
        String normalGetter = MetaProperty.getGetterName(propertyName, Object)
        String booleanGetter = MetaProperty.getGetterName(propertyName, Boolean)

        Method method = type.declaredMethods.find { it.name == normalGetter || it.name == booleanGetter }

        if (method) {
            return method.genericReturnType
        }

        Field field = type.declaredFields.find { it.name == propertyName }

        if (field) {
            return field.genericType
        }

        if (type.superclass) {
            return getGenericPropertyType(type.superclass, propertyName)
        }

        return Object
    }

    @Override
    Iterable<PropertyMetadata> getPersistentProperties() {
        collectPersistentProperties()
        return persistentProperties.values().asImmutable()
    }

    static AnnotatedElement getAnnotatedElement(Class type, String propertyName) {
        String normalGetter = MetaProperty.getGetterName(propertyName, Object)
        String booleanGetter = MetaProperty.getGetterName(propertyName, Boolean)

        Method method = type.declaredMethods.find { it.name == normalGetter || it.name == booleanGetter }

        if (method) {
            return method
        }

        Field field = type.declaredFields.find { it.name == propertyName }

        if (field) {
            return field
        }

        if (type.superclass) {
            return getAnnotatedElement(type.superclass, propertyName)
        }

        return null
    }

    static <T extends Annotation> T getAnnotation(Class type, String propertyName, Class<T> anno) {
        String normalGetter = MetaProperty.getGetterName(propertyName, Object)
        String booleanGetter = MetaProperty.getGetterName(propertyName, Boolean)

        Method method = type.declaredMethods.find { it.name == normalGetter || it.name == booleanGetter }

        if (method && method.getAnnotation(anno)) {
            return method.getAnnotation(anno)
        }

        Field field = type.declaredFields.find { it.name == propertyName }

        if (field && field.getAnnotation(anno)) {
            return field.getAnnotation(anno)
        }

        if (type.superclass) {
            return getAnnotation(type.superclass, propertyName, anno)
        }

        return null
    }

    @Override
    PropertyMetadata getPersistentProperty(String name) {
        collectPersistentProperties()
        return this.@persistentProperties[name]
    }

    @Override
    Object getId(Map<String, Object> fixture) {
        return fixture.id ?: fixture.name
    }

    @Override
    Set<String> getIdPropertyNames() {
        return ImmutableSet.of('id')
    }

    private Map<String, PropertyMetadata> collectPersistentProperties() {
        if (this.@persistentProperties != null) {
            return this.@persistentProperties
        }

        Map<String, PropertyMetadata> persistentProperties = [:].withDefault { String name ->
            MetaProperty property = type.metaClass.getMetaProperty(name)
            return property ? createPropertyMetadata(property) : null
        }

        type.metaClass.properties.findAll {
            isPersistent(type, it.name)
        }.collect {
            persistentProperties[it.name] = createPropertyMetadata(it)
        }

        return this.@persistentProperties = persistentProperties
    }

    protected PropertyMetadata createPropertyMetadata(MetaProperty it) {
        new PojoPropertyMetadata(type, it.name, isPersistent(type, it.name))
    }

    @SuppressWarnings('Instanceof')
    protected boolean isPersistent(Class type, String name) {
        AnnotatedElement element = getAnnotatedElement(type, name)
        if (!element) {
            return false
        }

        if (element instanceof Field) {
            return !Modifier.isFinal(element.modifiers)
        }

        return type.declaredMethods.any { it.name == MetaProperty.getSetterName(name) }
    }
}
