package com.agorapulse.dru;

import java.io.File;
import java.io.InputStream;

class DefaultSource extends AbstractSource {

    DefaultSource(Object referenceObject, String path) {
        super(referenceObject, path);
    }

    @Override
    public InputStream getSourceStream() {
        Class reference = getReferenceObject() instanceof Class ? (Class) getReferenceObject() : getReferenceObject().getClass();
        String path = reference.getSimpleName() + File.separator + this.getPath();
        InputStream stream = reference.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("Source '" + path + "' not found relative to " + reference);
        }
        return stream;
    }

    @Override
    public String toString() {
        return "DefaultSource[" + getPath() + "] relative to " + getReferenceObject();
    }

}
