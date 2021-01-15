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

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.util.function.Consumer;

public interface SourceDefinition {
    default SourceDefinition map(
        String path,
        @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.dru.PropertyMappingDefinition")
            Closure<PropertyMappingDefinition> configuration
    ) {
        return map(path, ConsumerWithDelegate.create(configuration));
    }

    default SourceDefinition map(
        @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.dru.PropertyMappingDefinition")
            Closure<PropertyMappingDefinition> configuration
    ) {
        return map(ConsumerWithDelegate.create(configuration));
    }

    default SourceDefinition map(Consumer<PropertyMappingDefinition> configuration) {
        return map("", configuration);
    }

    SourceDefinition map(String path, Consumer<PropertyMappingDefinition> configuration);
}
