package com.agorapulse.dru;

import com.agorapulse.dru.parser.Parser;
import com.agorapulse.dru.parser.Parsers;
import com.agorapulse.dru.persistence.Client;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.*;

final class DefaultDataSet implements DataSet {

    private final Map<Class, Map<String, Object>> createdEntities = new LinkedHashMap<>();
    private final MissingPropertiesReport report = new MissingPropertiesReport();
    private final Set<Client> clients;
    private final Set<DataSetMappingDefinition.WhenLoaded> whenLoadedListeners = new LinkedHashSet<>();

    DefaultDataSet(Set<Client> clients) {
        this.clients = clients;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findByTypeAndOriginalId(Class<T> type, Object originalId) {
        Map<String, Object> instancesByType = createdEntities.get(type);
        if (instancesByType == null) {
            return null;
        }
        return (T) instancesByType.get(String.valueOf(originalId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findAllByType(Class<T> type) {
        Map<String, Object> instancesByType = createdEntities.get(type);
        if (instancesByType == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>((Collection<T>) instancesByType.values()));
    }

    @Override
    public <T> T findByType(Class<T> type) {
        List<T> ret = findAllByType(type);
        if (ret != null && !ret.isEmpty()) {
            return ret.get(0);
        }
        return null;
    }

    @Override
    public <T> T add(Class<T> type, Object originalId, T entity) {
        Map<String, Object> instancesByType = createdEntities.get(type);
        if (instancesByType == null) {
            instancesByType = new LinkedHashMap<>();
            createdEntities.put(type, instancesByType);
        }
        instancesByType.put(String.valueOf(originalId), entity);
        return entity;
    }

    @Override
    public <T> void remove(Class<T> type, Object originalId) {
        Map<String, Object> instancesByType = createdEntities.get(type);
        if (instancesByType == null) {
            return;
        }
        instancesByType.remove(String.valueOf(originalId));
    }

    @Override
    public DataSet load(PreparedDataSet first, PreparedDataSet... others) {
        DefaultDataSetMapping dataSetMapping = new DefaultDataSetMapping(clients);
        first.executeOn(dataSetMapping);
        for (PreparedDataSet dataSet : others) {
            dataSet.executeOn(dataSetMapping);
        }
        return load(dataSetMapping);
    }
    @Override
    public DataSet load(DataSetMapping first, DataSetMapping... rest) {
        loadInternal(first);

        for (DataSetMapping mapping : rest) {
            loadInternal(mapping);
        }

        return loaded();
    }

    /**
     * Signals that data sets was manually loaded or the data has been changed significantly.
     * @return self
     */
    @Override
    public DataSet loaded() {
        for (DataSetMappingDefinition.WhenLoaded whenLoaded : whenLoadedListeners) {
            whenLoaded.doWhenLoaded(this);
        }
        return this;
    }

    @Override
    public DataSet load(@DelegatesTo(value = DataSetMappingDefinition.class, strategy = Closure.DELEGATE_FIRST) Closure<DataSetMappingDefinition> configuration) {
        return load(new PreparedDataSet(configuration));
    }

    @Override
    public MissingPropertiesReport getReport() {
        return report;
    }

    private void loadInternal(DataSetMapping mapping) {
        for (Source source : mapping.getSources().values()) {
            Parser parser = Parsers.findParser(source);
            Object content = parser.getContent(source);
            for (PropertyMapping propertyMapping : source.getRootPropertyMappings()) {
                String path = propertyMapping.getPath();
                Iterable<Map<String, Object>> matching = parser.findAllMatching(content, path);
                for (Map<String, Object> match : matching) {
                    propertyMapping.processPropertyValue(this, mapping, parser, null, match);
                }
            }
        }
        whenLoadedListeners.addAll(mapping.getWhenLoadedListeners());
    }
}
