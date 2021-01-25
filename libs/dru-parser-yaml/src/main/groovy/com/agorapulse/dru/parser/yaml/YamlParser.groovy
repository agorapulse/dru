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
package com.agorapulse.dru.parser.yaml

import com.agorapulse.dru.Source
import com.agorapulse.dru.parser.AbstractParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

/**
 * YML parser based on Jackson.
 */
class YamlParser extends AbstractParser {

    final int index = 20000

    private final ObjectMapper mapper

    YamlParser() {
        mapper = initMapper()
    }

    @Override
    boolean isSupported(Source source) {
        return source.path.endsWith('.yml') || source.path.endsWith('.yaml')
    }

    @Override
    Object getContent(Source source) {
        mapper.readValue(source.sourceStream, Object)
    }

    protected <T> T doConvertValue(Object value, Class<T> desiredType) {
        mapper.convertValue(value, desiredType)
    }

    private static ObjectMapper initMapper() {
        return new ObjectMapper(new YAMLFactory())
    }

}
