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

/**
 * Minimal metadata derived from GrailsDomainClassProperty.
 */
public interface PropertyMetadata {

    /**
     * Returns the name of the property.
     * @return The property name
     */
    String getName();

    /**
     * Returns the type for the domain class
     * @return  The property type
     */
    @SuppressWarnings("rawtypes")
    Class getType();

    /**
     * Returns the referenced property type. This differs from getType() in that in
     * the case of an Association it will return the type of the elements contained within the Collection,
     * otherwise it will delegate to getType();
     *
     * @return The referenced type
     */
    @SuppressWarnings("rawtypes")
    Class getReferencedPropertyType();

    /**
     * Returns true if the property is a persistent property.
     * @return Whether the property is persistent
     */
    boolean isPersistent();

    /**
     * Returns true if the property is a one-to-many relationship.
     * @return Whether it is a oneToMany
     */
    boolean isOneToMany();

    /**
     * Returns true if the property is a many-to-one relationship.
     * @return Whether it is a manyToOne
     */
    boolean isManyToOne();

    /**
     * Returns true if the property is a many-to-many relationship.
     * @return true if it is a manyToMany
     */
    boolean isManyToMany();

    /**
     * Returns true if the property is a one-to-one relationship.
     * @return true if it is a one-to-one relationship
     */
    boolean isOneToOne();

    /**
     * Returns true if this property is a relationship property.
     * @return true if it is an associative property
     */
    boolean isAssociation();

    /**
     * Whether this side of the association is the "owning" side.
     *
     * @return true if it is the owning side
     */
    boolean isOwningSide();

    /**
     * Retrieves the name of property referenced by this property if it is
     * an association and is known, otherwise null.
     *
     * @return The name of the prop
     */
    String getReferencedPropertyName();

    /**
     * Returns true if this property is an embedded component.
     *
     * @return true if it is, false otherwise
     */
    boolean isEmbedded();

    /**
     * Return whether this is a collection of basic types like String, Integer etc.
     * @return true if it is a collection of basic types
     */
    boolean isBasicCollectionType();

    boolean isCollectionType();

}
