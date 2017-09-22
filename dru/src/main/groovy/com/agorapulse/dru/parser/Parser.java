package com.agorapulse.dru.parser;

import com.agorapulse.dru.Source;

import java.util.Map;

public interface Parser {

    int getIndex();

    boolean isSupported(Source relativePath);

    Object getContent(Source source);

    /**
     * Asks the source to help covert the value to desired type.
     * @param <T> desired type
     * @param path path to the value
     * @param value string representation of object
     * @param desiredType desired type
     * @return object of desired type created based on the string representation
     */
    <T> T convertValue(String path, Object value, Class<T> desiredType);

    Iterable<Map<String, Object>> findAllMatching(Object content, String path);
}
