package com.agorapulse.dru

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper

final class DataSetGuardian implements DataSet {

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
            return MAPPER.readValue(MAPPER.writeValueAsString(object), object.getClass())
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
        List<T> copy = new ArrayList<>()
        for (T item : original.findAllByType(type)) {
            copy.add(deepClone(item))
        }
        return Collections.unmodifiableList(copy)
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
    DataSet load(Closure<DataSetMappingDefinition> configuration) {
        return original.load(configuration)
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
        return original.getReport()
    }
}
