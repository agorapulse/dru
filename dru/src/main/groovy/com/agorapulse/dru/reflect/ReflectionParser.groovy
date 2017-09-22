package com.agorapulse.dru.reflect

import com.agorapulse.dru.Source
import com.agorapulse.dru.parser.AbstractParser

/**
 * Parser which handles properties of reference object as sources.
 */
class ReflectionParser extends AbstractParser {

    final int index = Integer.MAX_VALUE - 10000

    @Override
    boolean isSupported(Source source) {
        return !source.path.contains('.') && source.referenceObject.hasProperty(source.path)
    }

    @Override
    Object getContent(Source source) {
        return source.referenceObject."$source.path"
    }

}
