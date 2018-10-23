package com.agorapulse.dru;

import java.io.*;

class FileSource extends AbstractSource {

    private final File file;

    FileSource(Object referenceObject, File file) throws IOException {
        super(referenceObject, file.getCanonicalPath());
        this.file = file;
    }

    @Override
    public InputStream getSourceStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File does not exist", e);
        }
    }

    public String toString() {
        return "FileSource[" + file.getAbsolutePath()  + "] relative to " + getReferenceObject();
    }
}
