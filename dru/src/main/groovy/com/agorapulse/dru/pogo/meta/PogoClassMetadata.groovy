package com.agorapulse.dru.pogo.meta

import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata

import java.lang.annotation.Annotation
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class PogoClassMetadata implements ClassMetadata {

    final Class type
    private Map<String, PropertyMetadata> persistentProperties

    PogoClassMetadata(Class type) {
        this.type = type
    }

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

        Method method = type.getDeclaredMethods().find {it.name == normalGetter || it.name == booleanGetter }

        if (method) {
            return method.genericReturnType
        }

        Field field = type.getDeclaredFields().find { it.name == propertyName }

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

        Method method = type.getDeclaredMethods().find {it.name == normalGetter || it.name == booleanGetter }

        if (method) {
            return method
        }

        Field field = type.getDeclaredFields().find { it.name == propertyName }

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

        Method method = type.getDeclaredMethods().find {it.name == normalGetter || it.name == booleanGetter }

        if (method && method.getAnnotation(anno)) {
            return method.getAnnotation(anno)
        }

        Field field = type.getDeclaredFields().find { it.name == propertyName }

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
        return null
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
        new PogoPropertyMetadata(type, it.name, isPersistent(type, it.name))
    }

    protected boolean isPersistent(Class type, String name) {
        AnnotatedElement element = getAnnotatedElement(type, name)
        if (!element) {
            return false
        }

        if (element instanceof Field) {
            return !Modifier.isFinal(element.modifiers)
        }

        return type.getDeclaredMethods().any { it.name == MetaProperty.getSetterName(name) }
    }
}
