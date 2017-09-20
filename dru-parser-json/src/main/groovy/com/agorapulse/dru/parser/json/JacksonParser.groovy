package com.agorapulse.dru.parser.json

import com.agorapulse.dru.Source
import com.agorapulse.dru.parser.AbstractParser
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Json parser based on Jackson.
 */
class JacksonParser extends AbstractParser {

    final int index = 10000

    private final ObjectMapper mapper

    JacksonParser() {
        mapper = initMapper()
    }

    @Override
    boolean isSupported(String relativePath) {
        return relativePath.endsWith('.json')
    }

    @Override
    Object getContent(Source source) {
        mapper.readValue(source.sourceStream, Object)
    }

    protected <T> T doConvertValue(Object value, Class<T> desiredType) {
        mapper.convertValue(value, desiredType)
    }

    private static ObjectMapper initMapper() {
        return new ObjectMapper()
    }

}
