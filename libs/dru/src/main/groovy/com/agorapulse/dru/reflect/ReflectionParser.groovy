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
package com.agorapulse.dru.reflect

import com.agorapulse.dru.Source
import com.agorapulse.dru.parser.AbstractParser

/**
 * Parser which handles properties of reference object as sources.
 */
class ReflectionParser extends AbstractParser {

    private static final int INDEX = Integer.MAX_VALUE - 10000;

    @Override
    int getIndex() {
        return INDEX;
    }

    @Override
    @SuppressWarnings('Instanceof')
    boolean isSupported(Source source) {
        if (source.path.contains('.')) {
            return false
        }

        if (source.referenceObject instanceof Class) {
            Class type = source.referenceObject
            return type.fields.any { it.name == source.path }
        }

        return source.referenceObject.hasProperty(source.path)
    }

    @Override
    Object getContent(Source source) {
        return source.referenceObject."$source.path"
    }

}
