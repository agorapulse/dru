package com.agorapulse.dru

import groovy.transform.PackageScope
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

/**
 * Mapping from source to specified object of given type.
 * @param <T> type of the destination object
 */
class TypeMapping<T> implements TypeMappingDefinition<T> {

    private final String path
    private final Class<T> type
    private final List<Closure<Boolean>> conditions = []
    private final Set<String> ignored = new LinkedHashSet<String>()

    // sets the value directly to the newly created instance if the value is falsy
    private final Customisations defaultsCustomisation = new Customisations()

    // overrides the value in the incoming map, e.g. type tweaks
    private final Customisations overridesCustomisation = new Customisations()

    private final PropertyMappings propertyMappings

    private Closure query = Closure.IDENTITY

    @PackageScope String name

    TypeMapping(String path, Class<T> type) {
        this.path = path
        this.type = type
        this.propertyMappings = new PropertyMappings(path)
    }

    TypeMapping<T> when(
        @DelegatesTo(type = 'java.util.Map<String,Object>', strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString, options = 'java.util.Map<String, Object>')
        Closure<Boolean> condition
    ) {
        conditions << condition
        return this
    }

    TypeMapping<T> and(
        @DelegatesTo(type = 'java.util.Map<String,Object>', strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString, options = 'java.util.Map<String, Object>')
        Closure<Boolean> condition
    ) {
        when(condition)
    }

    TypeMapping<T> defaults(
        @DelegatesTo(type = 'T', strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString, options = 'java.util.Map<String, Object>')
        Closure defaultsSetter
    ) {
        defaultsCustomisation.add defaultsSetter
        return this
    }

    TypeMapping<T> overrides(
        @DelegatesTo(type = 'T', strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString, options = 'java.util.Map<String, Object>')
        Closure defaultsSetter
    ) {
        overridesCustomisation.add defaultsSetter
        return this
    }

    @Override
    TypeMappingDefinition<T> just(
        @DelegatesTo(type = 'T', strategy = Closure.DELEGATE_ONLY)
        @ClosureParams(value = FromString, options = 'T')
        Closure query
    ) {
        this.query = query
        return this
    }

    TypeMapping<T> ignore(Iterable<String> ignored) {
        this.@ignored.addAll(ignored)
        return this
    }

    TypeMapping<T> ignore(String first, String... rest) {
        ignored << first
        if (rest) {
            this.@ignored.addAll(rest)
        }
        return this
    }

    @Override
    @SuppressWarnings('UnnecessaryGetter')
    TypeMappingDefinition<T> ignore(@DelegatesTo(type = 'T', strategy = Closure.DELEGATE_ONLY) Closure ignoreConfigurer) {
        MockObject map = new MockObject()
        Customisations.prepare(ignoreConfigurer, map)()
        this.@ignored.addAll(map.getAccessedKeys())
        return this
    }

    PropertyMapping map(String path) {
        return propertyMappings.findOrCreate(path)
    }

    TypeMapping<T> map(String path, @DelegatesTo(value = PropertyMapping, strategy = Closure.DELEGATE_FIRST) Closure<PropertyMappingDefinition> configuration) {
        PropertyMapping mapping = propertyMappings.findOrCreate(path)
        mapping.with configuration
        return this
    }

    @SuppressWarnings('UnnecessaryDotClass') // for some reason when I remove the .class it no longer compiles
    TypeMapping<T> map(Iterable<String> paths,
         @DelegatesTo(value = PropertyMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
         Closure<PropertyMappingDefinition> configuration
    ) {
        for (String path in paths) {
            map(path, configuration)
        }
        return this
    }

    Class<T> getType() {
        return type
    }

    String getName() {
        return name
    }

    List<Closure<Boolean>> getConditions() {
        return conditions.asImmutable()
    }

    Customisations getDefaults() {
        return defaultsCustomisation
    }

    Customisations getOverrides() {
        return overridesCustomisation
    }

    Set<String> getIgnored() {
        return ignored.asImmutable()
    }

    PropertyMappings getPropertyMappings() {
        return propertyMappings
    }

    Object process(T result) {
        Customisations.prepare(query, result)(result)
    }

    @Override
    String toString() {
        return "TypeMapping[$type.simpleName]"
    }
}
