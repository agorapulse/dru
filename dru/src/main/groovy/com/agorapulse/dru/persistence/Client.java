package com.agorapulse.dru.persistence;

import com.agorapulse.dru.parser.Parser;
import com.agorapulse.dru.persistence.meta.ClassMetadata;

import java.util.Map;

public interface Client {

    boolean isSupported(Class type);

    ClassMetadata getClassMetadata(Class type);

    <T> T newInstance(Parser parser, Class<T> type, Map<String, Object> payload);
    <T> T save(T object);
    String getId(Object object);
    String getId(Class type, Map<String, Object> properties);
    <T> T addTo(T object, String association, Object other);

}
