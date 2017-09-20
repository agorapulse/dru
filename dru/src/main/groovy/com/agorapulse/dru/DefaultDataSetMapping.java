package com.agorapulse.dru;

import com.agorapulse.dru.persistence.Client;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.*;

final class DefaultDataSetMapping implements DataSetMappingDefinition, DataSetMapping {

    private Set<Integer> included = new HashSet<>();

    public DefaultDataSetMapping(Set<Client> clients) {
        this.clients = Collections.unmodifiableSet(clients);
    }

    @Override
    public DataSetMappingDefinition from(String relativePath, @DelegatesTo(value = SourceDefinition.class, strategy = Closure.DELEGATE_FIRST) Closure<SourceDefinition> configuration) {
        Source source = sources.get(relativePath);
        if (source == null) {
            Source newSource = new DefaultSource(configuration.getThisObject(), relativePath);
            sources.put(relativePath, newSource);
            source = newSource;
        }

        DefaultGroovyMethods.with(source, configuration);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> DataSetMappingDefinition any(Class<T> type, @DelegatesTo(type = "com.agorapulse.dru.TypeMappingDefinition<T>", strategy = Closure.DELEGATE_FIRST) @ClosureParams(value = groovy.transform.stc.FromString.class, options = "com.agorapulse.dru.TypeMappingDefinition<T>") Closure<TypeMappingDefinition<T>> configuration) {
        DefaultGroovyMethods.with(typeMappings.findOrCreate(type, type.getSimpleName()), configuration);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> DataSetMappingDefinition any(Class<T> type) {
        return any(type, Closure.IDENTITY);
    }

    @Override
    public DataSetMappingDefinition include(PreparedDataSet plan) {
        int hashCode = System.identityHashCode(plan);
        if (included.contains(hashCode)) {
            return this;
        }
        included.add(hashCode);
        plan.executeOn(this);
        return this;
    }

    @Override
    public Map<String, Source> getSources() {
        return Collections.unmodifiableMap(sources);
    }

    @Override
    public List<WhenLoaded> getWhenLoadedListeners() {
        return Collections.unmodifiableList(whenLoadedListeners);
    }

    @Override
    public Set<Client> getClients() {
        return clients;
    }

    @Override
    public TypeMappings getTypeMappings() {
        return typeMappings;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void applyOverrides(Class type, Object destination, Object source) {
        typeMappings.applyOverrides(type, destination, source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void applyDefaults(Class<?> type, Object destination, Object source) {
        typeMappings.applyDefaults(type, destination, source);

    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isIgnored(Class<?> type, String propertyName) {
        return typeMappings.isIgnored(type, propertyName);
    }

    @Override
    public DataSetMappingDefinition whenLoaded(WhenLoaded listener) {
        whenLoadedListeners.add(listener);
        return this;
    }

    private final Set<Client> clients;
    private final Map<String, Source> sources = new LinkedHashMap<>();
    private final TypeMappings typeMappings = new TypeMappings();
    private final List<WhenLoaded> whenLoadedListeners = new ArrayList<>();

}
