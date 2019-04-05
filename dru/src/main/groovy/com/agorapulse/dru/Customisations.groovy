package com.agorapulse.dru

import java.util.function.BiConsumer

/**
 * Collect closures customising some object to be able to apply them at once later.
 */
class Customisations {

    private final List<BiConsumer> setters = []

    void apply(Object destination, Object source) {
        setters.each {
            it.accept(destination, source)
        }
    }

    void add(BiConsumer biConsumer) {
        setters << biConsumer
    }

}
