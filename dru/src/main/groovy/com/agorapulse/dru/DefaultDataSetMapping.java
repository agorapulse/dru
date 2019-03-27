package com.agorapulse.dru;

import com.agorapulse.dru.persistence.Client;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

final class DefaultDataSetMapping implements DataSetMappingDefinition, DataSetMapping {

    private Set<Integer> included = new HashSet<>();

    DefaultDataSetMapping(Object unitTest, Set<Client> clients) {
        this.unitTest = unitTest;
        this.clients = Collections.unmodifiableSet(clients);
    }

    @Override
    public DataSetMappingDefinition from(String relativePath, Consumer<SourceDefinition> configuration) {
        AbstractSource source = sources.get(relativePath);
        if (source == null) {
            AbstractSource newSource = new DefaultSource(unitTest, relativePath);
            sources.put(relativePath, newSource);
            source = newSource;
        }

        configuration.accept(source);
        return this;
    }

    @Override
    public DataSetMappingDefinition from(File file, Consumer<SourceDefinition> configuration) throws IOException {
        AbstractSource source = sources.get(file.getCanonicalPath());
        if (source == null) {
            AbstractSource newSource = new FileSource(unitTest, file);
            sources.put(file.getCanonicalPath(), newSource);
            source = newSource;
        }

        configuration.accept(source);
        return this;
    }

    @Override
    public <T> DataSetMappingDefinition any(Class<T> type, Consumer<TypeMappingDefinition<T>> configuration) {
        configuration.accept(typeMappings.findOrCreate(type, type.getSimpleName()));
        return this;
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
    public List<OnChange> getOnChangeListeners() {
        return Collections.unmodifiableList(onChangeListeners);
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

    @Override
    public DataSetMappingDefinition onChange(OnChange listener) {
        onChangeListeners.add(listener);
        return this;
    }

    private final Object unitTest;
    private final Set<Client> clients;
    private final Map<String, AbstractSource> sources = new LinkedHashMap<>();
    private final TypeMappings typeMappings = new TypeMappings();
    private final List<WhenLoaded> whenLoadedListeners = new ArrayList<>();
    private final List<OnChange> onChangeListeners = new ArrayList<>();

}
