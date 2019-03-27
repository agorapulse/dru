package com.agorapulse.dru

import groovy.transform.PackageScope

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

    @Deprecated
    @PackageScope static Closure prepare(Closure closure, Object delegate) {
        Closure clone = closure.clone() as Closure
        clone.resolveStrategy = Closure.DELEGATE_ONLY
        clone.delegate = delegate
        return clone
    }

}
