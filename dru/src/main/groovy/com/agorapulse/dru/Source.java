package com.agorapulse.dru;

import java.io.InputStream;

public interface Source {

    String getPath();

    /**
     * @return the reference object is used to determine the path to the source
     */
    Object getReferenceObject();

    InputStream getSourceStream();

    Iterable<PropertyMapping> getRootPropertyMappings();
}
