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
package com.agorapulse.dru.mn.data.jdbc.parser;

import com.agorapulse.dru.reflect.ReflectionParser;
import io.micronaut.core.convert.ConversionService;

/**
 * Replaces default reflection parser to use {@link ConversionService} for conversion.
 */
public class MicronautReflectionParser extends ReflectionParser {

    private static final int INDEX = Integer.MAX_VALUE - 20000;

    public int getIndex() {
        return INDEX;
    }

    @Override
    protected <T> T doConvertValue(Object value, Class<T> desiredType) {
        return ConversionService.SHARED.convertRequired(value, desiredType);
    }

}
