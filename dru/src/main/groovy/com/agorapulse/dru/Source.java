package com.agorapulse.dru;

import java.io.InputStream;

public interface Source {

    String getPath();

    InputStream getSourceStream();

    Object getReferenceObject();

    Iterable<PropertyMapping> getRootPropertyMappings();
}
