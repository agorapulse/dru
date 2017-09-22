package com.agorapulse.dru.pojo.meta

import com.agorapulse.dru.persistence.meta.PropertyMetadata

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Describes property of POJO.
 */
class PojoPropertyMetadata implements PropertyMetadata {

    protected final Class clazz
    protected final String name
    protected final boolean persistent

    String referencedPropertyName = null

    PojoPropertyMetadata(Class type, String name, boolean persistent) {
        this.name = name
        this.clazz = type
        this.persistent = persistent
    }

    @Override
    String getName() {
        return name
    }

    @Override
    Class getType() {
        return PojoClassMetadata.getPropertyType(clazz, name)
    }

    @Override
    Class getReferencedPropertyType() {
        if (Collection.isAssignableFrom(type)) {
            Type ret = findItemType(PojoClassMetadata.getGenericPropertyType(clazz, name))
            if (ret) {
                return ret as Class
            }
            return Object
        }
        return type
    }

    @Override
    boolean isPersistent() {
        return persistent
    }

    @Override
    boolean isOneToMany() {
        return false
    }

    @Override
    boolean isManyToOne() {
        return false
    }

    @Override
    boolean isManyToMany() {
        return false
    }

    @Override
    boolean isOneToOne() {
        return false
    }

    @Override
    boolean isAssociation() {
        return false
    }

    @Override
    boolean isOwningSide() {
        return true
    }

    @Override
    boolean isEmbedded() {
        return false
    }

    @Override
    boolean isBasicCollectionType() {
        return Collection.isAssignableFrom(type)
    }

    @Override
    boolean isCollectionType() {
        return isBasicCollectionType() || isManyToMany() || isOneToMany()
    }

    @SuppressWarnings('Instanceof')
    private static Class findItemType(Type type) {
        if (!type || type == Object) {
            return null
        }

        if (type instanceof ParameterizedType) {
            if (Collection.isAssignableFrom(type.rawType as Class)) {
                Class found = type.actualTypeArguments.find {
                    it instanceof Class
                }
                if (found) {
                    return found
                }
            }
        }

        if (!(type instanceof Class)) {
            return null
        }

        Class clazz = type as Class

        for (ParameterizedType iface in clazz.genericInterfaces.findAll { it instanceof ParameterizedType }) {
            Class found = findItemType(iface)
            if (found) {
                return found
            }
        }
        return findItemType(clazz.genericSuperclass)
    }
}
