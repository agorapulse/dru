package com.agorapulse.dru

class MissingProperty {

    static MissingProperty create(String path, String propertyName, Object value, Class type) {
        return new MissingProperty(path, propertyName, value, type)
    }

    final String path
    final String propertyName
    final Object value
    final Class type

    private MissingProperty(String path, String propertyName, Object value, Class type) {
        this.path = path
        this.propertyName = propertyName
        this.value = value
        this.type = type
    }

    @Override
    String toString() {
        "PropertyMissing: path=$path, type=$type, value=$value"
    }
}
