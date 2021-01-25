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
package com.agorapulse.dru;

import java.util.function.Consumer;

abstract class AbstractSource implements SourceDefinition, Source {

    AbstractSource(Object referenceObject, String path) {
        this.referenceObject = referenceObject;
        this.path = path;
        this.propertyMappings = new PropertyMappings(path);
    }

    @Override
    public final SourceDefinition map(String path, Consumer<PropertyMappingDefinition> configuration
    ) {
        PropertyMapping mapping = propertyMappings.findOrCreate(path);
        configuration.accept(mapping);
        return this;
    }

    @Override
    public final String getPath() {
        return path;
    }

    public final Object getReferenceObject() {
        return referenceObject;
    }

    @Override
    public final PropertyMappings getRootPropertyMappings() {
        return propertyMappings;
    }

    private final Object referenceObject;
    private final String path;
    private final PropertyMappings propertyMappings;
}
