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
package com.agorapulse.dru

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper

import java.util.function.Consumer

/**
 * Data set adapter which guarantees deep cloning all the saved and retrieved objects.
 */
class DataSetGuardian implements DataSet {

    @SuppressWarnings('Instanceof')
    static DataSet guard(DataSet dataSet) {
        if (dataSet instanceof DataSetGuardian) {
            return dataSet
        }
        if (dataSet instanceof Dru) {
            // guard the nested data set
            return guard(((Dru) dataSet).load())
        }
        return new DataSetGuardian(dataSet)
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .configure(JsonParser.Feature.ALLOW_COMMENTS, true)

    private final DataSet original

    private DataSetGuardian(DataSet original) {
        this.original = original
    }

    private static <T> T deepClone(T object) {
        if (object == null) {
            return null
        }
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(object), (Class<T>) object.getClass())
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot clone $object", e)
        }
    }

    @Override
    <T> T findByTypeAndOriginalId(Class<T> type, Object id) {
        return deepClone(original.findByTypeAndOriginalId(type, id))
    }

    @Override
    <T> List<T> findAllByType(Class<T> type) {
        return original.findAllByType(type).collect(this.&deepClone).asImmutable() as List<T>
    }

    @Override
    <T> T findByType(Class<T> type) {
        return deepClone(original.findByType(type))
    }

    @Override
    <T> T add(T entity) {
        original.add(deepClone(entity))
        return entity
    }

    @Override
    <T> T add(T entity, Object manualId) {
        original.add(deepClone(entity), manualId)
        return entity
    }

    @Override
    <T> T remove(T object) {
        return original.remove(object)
    }

    @Override
    DataSet load(PreparedDataSet first, PreparedDataSet... rest) {
        return original.load(first, rest)
    }

    @Override
    DataSet load(Class<?> self, Consumer<DataSetMappingDefinition> configuration) {
        return original.load(self, configuration)
    }

    @Override
    DataSet loaded() {
        return original.loaded()
    }

    @Override
    DataSet changed() {
        return original.changed()
    }

    @Override
    MissingPropertiesReport getReport() {
        return original.report
    }
}
