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
package com.agorapulse.dru.persistence.meta;

public class CachedPropertyMetadata extends AbstractPropertyMetadata {

    private final String name;
    private final Class type;
    private final Class referencedPropertyType;
    private final boolean persistent;
    private final boolean oneToMany;
    private final boolean manyToOne;
    private final boolean manyToMany;
    private final boolean oneToOne;
    private final boolean association;
    private final boolean owningSide;
    private final String referencedPropertyName;
    private final boolean embedded;
    private final boolean basicCollectionType;
    private final PropertyMetadata original;

    CachedPropertyMetadata(PropertyMetadata original) {
        this.name = original.getName();
        this.type = original.getType();
        this.referencedPropertyType = original.getReferencedPropertyType();
        this.persistent = original.isPersistent();
        this.oneToMany = original.isOneToMany();
        this.manyToOne = original.isManyToOne();
        this.manyToMany = original.isManyToMany();
        this.oneToOne = original.isOneToOne();
        this.association = original.isAssociation();
        this.owningSide = original.isOwningSide();
        this.referencedPropertyName = original.getReferencedPropertyName();
        this.embedded = original.isEmbedded();
        this.basicCollectionType = original.isBasicCollectionType();
        this.original = original;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public Class getReferencedPropertyType() {
        return referencedPropertyType;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public boolean isOneToMany() {
        return oneToMany;
    }

    @Override
    public boolean isManyToOne() {
        return manyToOne;
    }

    @Override
    public boolean isManyToMany() {
        return manyToMany;
    }

    @Override
    public boolean isOneToOne() {
        return oneToOne;
    }

    public boolean isAssociation() {
        return association;
    }

    @Override
    public boolean isOwningSide() {
        return owningSide;
    }

    @Override
    public String getReferencedPropertyName() {
        return referencedPropertyName;
    }

    @Override
    public boolean isEmbedded() {
        return embedded;
    }

    @Override
    public boolean isBasicCollectionType() {
        return basicCollectionType;
    }

    public PropertyMetadata getOriginal() {
        return original;
    }
}
