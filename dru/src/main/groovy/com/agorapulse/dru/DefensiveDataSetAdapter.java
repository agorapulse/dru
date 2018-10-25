package com.agorapulse.dru;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Closure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DefensiveDataSetAdapter implements DataSet {

    public static DataSet guard(DataSet dataSet) {
        if (dataSet instanceof DefaultDataSet) {
            return dataSet;
        }
        if (dataSet instanceof Dru) {
            // guard the nested data set
            return guard(((Dru) dataSet).load());
        }
        return new DefensiveDataSetAdapter(dataSet);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    private final DataSet original;

    private DefensiveDataSetAdapter(DataSet original) {
        this.original = original;
    }

    private static <T> T deepClone(T object) {
        if (object == null) {
            return null;
        }
        try {
            return (T) MAPPER.readValue(MAPPER.writeValueAsString(object), object.getClass());
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot clone " + object, e);
        }
    }

    @Override
    public <T> T findByTypeAndOriginalId(Class<T> type, Object id) {
        return deepClone(original.findByTypeAndOriginalId(type, id));
    }

    @Override
    public <T> List<T> findAllByType(Class<T> type) {
        List<T> copy = new ArrayList<>();
        for (T item : original.findAllByType(type)) {
            copy.add(deepClone(item));
        }
        return Collections.unmodifiableList(copy);
    }

    @Override
    public <T> T findByType(Class<T> type) {
        return deepClone(original.findByType(type));
    }

    @Override
    public <T> T add(T entity) {
        original.add(deepClone(entity));
        return entity;
    }

    @Override
    public <T> T add(T entity, Object manualId) {
        original.add(deepClone(entity), manualId);
        return entity;
    }

    @Override
    public <T> T remove(T object) {
        return original.remove(object);
    }

    @Override
    public DataSet load(PreparedDataSet first, PreparedDataSet... rest) {
        return original.load(first, rest);
    }

    @Override
    public DataSet load(Closure<DataSetMappingDefinition> configuration) {
        return original.load(configuration);
    }

    @Override
    public DataSet loaded() {
        return original.loaded();
    }

    @Override
    public DataSet changed() {
        return original.changed();
    }

    @Override
    public MissingPropertiesReport getReport() {
        return original.getReport();
    }
}
