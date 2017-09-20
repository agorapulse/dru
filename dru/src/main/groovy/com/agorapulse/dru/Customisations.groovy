package com.agorapulse.dru

import groovy.transform.PackageScope

/**
 * Collect closures customising some object to be able to apply them at once later.
 */
class Customisations {

    private final List<Closure> setters = []

    void apply(Object destination, Object source) {
        setters.each {
            prepare(it, destination)(source)
        }
    }

    void add(Closure closure) {
        setters << closure
    }

    @PackageScope static Closure prepare(Closure closure, Object delegate) {
        Closure clone = closure.clone() as Closure
        clone.resolveStrategy = Closure.DELEGATE_ONLY
        clone.delegate = delegate
        return clone
    }

}
