package com.agorapulse.dru.parser

import com.google.common.primitives.Primitives

/**
 * Base class for parsers with convenient methods to convert values and to find all matching elements.
 */
@SuppressWarnings('AbstractClassWithoutAbstractMethod')
abstract class AbstractParser implements Parser {

    /**
     * Key for singleton map when the content is not map.
     */
    public static final String VALUE_KEY = 'value'

    @SuppressWarnings([
        'CatchException',
        'Instanceof',
    ])
    final <T> T convertValue(String path, Object value, Class<T> desiredType) {
        try {
            if (value == null) {
                if (desiredType.isPrimitive() && Number.isAssignableFrom(Primitives.wrap(desiredType))) {
                    return 0 as T
                }
                if (desiredType.isPrimitive() && Boolean.isAssignableFrom(Primitives.wrap(desiredType))) {
                    return false as T
                }
                if (desiredType.isPrimitive() && Character.isAssignableFrom(Primitives.wrap(desiredType))) {
                    return 0 as T
                }
                return null
            }
            if (value instanceof CharSequence && String.isAssignableFrom(desiredType)) {
                return value.toString() as T
            }
            doConvertValue(value, desiredType)
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert '$value' to '$desiredType' at '$path'", e)
        }
    }

    @SuppressWarnings([
        'Instanceof',
        'CouldBeSwitchStatement',
    ])
    Iterable<Map<String, Object>> findAllMatching(Object content, String path) {
        if (!path) {
            if (content instanceof Map) {
                return [content]
            }
            if (content instanceof Iterable) {
                return content.collectMany { findAllMatching(it, '') }
            }
            if (content == null) {
                return Collections.emptyList()
            }
            return Collections.singletonList(Collections.singletonMap(VALUE_KEY, content))
        }

        int indexOfComma = path.indexOf('.')

        if (indexOfComma == -1) {
            if (content instanceof Map) {
                Object found = content[path]
                if (found instanceof Map) {
                    return Collections.singletonList(found)
                }
                if (found instanceof Iterable) {
                    List<Map<String, Object>> ret = []
                    ret.addAll(found)
                    return ret.asImmutable()
                }
                if (found == null) {
                    return Collections.emptyList()
                }
                return Collections.singletonList(Collections.singletonMap(VALUE_KEY, found))
            }
            if (content instanceof Iterable) {
                List<Map<String, Object>> ret = []
                for (item in content) {
                    ret.addAll(findAllMatching(item, path))
                }
                return ret.asImmutable()
            }
            return Collections.emptyList()
        }

        String prefix = path[0..(indexOfComma - 1)]
        String rest = path[(indexOfComma + 1)..-1]
        Iterable<Object> sources = findAllMatching(content, prefix)

        List<Map<String, Object>> ret = []
        for (source in sources) {
            ret.addAll(findAllMatching(source, rest))
        }
        return ret.asImmutable()
    }

    protected <T> T doConvertValue(Object value, Class<T> desiredType) {
        value.asType(desiredType)
    }
}
