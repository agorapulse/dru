package com.agorapulse.dru

/**
 * Missing property is a missing property from the source which hasn't found its counterpart in any persistent property
 * and because of that the information it is holding is lost.
 *
 * Use TypeMapping#ignore() to explicitly declare property as one which can be ignored.
 */
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
}
