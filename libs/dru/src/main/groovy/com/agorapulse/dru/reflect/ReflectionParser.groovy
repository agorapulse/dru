package com.agorapulse.dru.reflect

import com.agorapulse.dru.Source
import com.agorapulse.dru.parser.AbstractParser

/**
 * Parser which handles properties of reference object as sources.
 */
class ReflectionParser extends AbstractParser {

    final int index = Integer.MAX_VALUE - 10000

    @Override
    @SuppressWarnings('Instanceof')
    boolean isSupported(Source source) {
        if (source.path.contains('.')) {
            return false
        }

        if (source.referenceObject instanceof Class) {
            Class type = source.referenceObject
            return type.fields.any { it.name == source.path }
        }

        return source.referenceObject.hasProperty(source.path)
    }

    @Override
    Object getContent(Source source) {
        return source.referenceObject."$source.path"
    }

}
