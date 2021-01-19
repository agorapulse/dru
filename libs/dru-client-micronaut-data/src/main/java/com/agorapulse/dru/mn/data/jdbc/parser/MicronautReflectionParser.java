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
