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
package com.agorapulse.dru.pojo.meta

import com.agorapulse.dru.persistence.meta.AbstractPropertyMetadata

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Describes property of POJO.
 */
class PojoPropertyMetadata extends AbstractPropertyMetadata {

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
        return toMany
    }

    @Override
    boolean isManyToOne() {
        return isAssociation() && !toMany
    }

    @Override
    boolean isManyToMany() {
        return toMany
    }

    @Override
    boolean isOneToOne() {
        return isAssociation() && !toMany
    }

    @Override
    boolean isAssociation() {
        return !isBasicType(referencedPropertyType)
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
        return !association && toMany
    }

    private boolean isToMany() {
        return Collection.isAssignableFrom(type)
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

    private static boolean isBasicType(Class type) {
        type.package?.name?.startsWith('java') || type.package?.name?.startsWith('groovy') || type.primitive
    }
}
