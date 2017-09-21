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
    boolean isSupported(String relativePath) {
        return relativePath.endsWith('.yml') || relativePath.endsWith('.yaml')
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
