package com.agorapulse.dru.pogo.meta

import com.agorapulse.dru.persistence.meta.PropertyMetadata

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class PogoPropertyMetadata implements PropertyMetadata {

    protected final Class clazz
    protected final String name
    protected final boolean persistent

    PogoPropertyMetadata(Class type, String name, boolean persistent) {
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
        return PogoClassMetadata.getPropertyType(clazz, name)
    }

    @Override
    Class getReferencedPropertyType() {
        if (Collection.isAssignableFrom(type)) {
            Type ret = findItemType(PogoClassMetadata.getGenericPropertyType(clazz, name))
            if (ret) {
                return ret as Class
            }
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
    String getReferencedPropertyName() {
        return null
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

        if ((!type instanceof Class)) {
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
