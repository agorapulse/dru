/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.dru;

import com.agorapulse.dru.parser.Parser;
import com.agorapulse.dru.parser.Parsers;
import com.agorapulse.dru.persistence.Client;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.function.Consumer;

final class DefaultDataSet implements DataSet {

    private final Map<Class, Map<String, Object>> createdEntities = new LinkedHashMap<>();
    private final MissingPropertiesReport report = new MissingPropertiesReport();
    private final Object unitTest;
    private final Set<Client> clients;
    private final Set<DataSetMappingDefinition.WhenLoaded> whenLoadedListeners = new LinkedHashSet<>();
    private final Set<DataSetMappingDefinition.OnChange> onChangeListeners = new LinkedHashSet<>();

    DefaultDataSet(Object unitTest, Set<Client> clients) {
        this.unitTest = unitTest;
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
    public <T> T add(T entity) {
        return add(entity, null);
    }

    @Override
    public <T> T add(T entity, Object manualId) {
        Map<String, Object> instancesByType = createdEntities.get(entity.getClass());
        if (instancesByType == null) {
            instancesByType = new LinkedHashMap<>();
            createdEntities.put(entity.getClass(), instancesByType);
        }

        String id = manualId != null ? String.valueOf(manualId) : findClient(entity.getClass()).getId(entity);
        String systemId = String.valueOf(System.identityHashCode(entity));

        if (id == null) {
            id = systemId;
        }

        if (!systemId.equals(id)) {
            instancesByType.remove(systemId);
            instancesByType.put(id, entity);
        } else {
            instancesByType.put(systemId, entity);
        }

        changed();

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T remove(T object) {
        Map<String, Object> instances = createdEntities.get(object.getClass());
        if (instances == null) {
            return null;
        }

        String key = findClient(object.getClass()).getId(object);

        T removed = (T) instances.remove(key);

        if (removed != null) {

            changed();

            return removed;
        }

        for (Map.Entry<String, Object> entry : instances.entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(object)) {
                key = entry.getKey();
                break;
            }
        }

        removed = key != null ? (T) instances.remove(key) : null;

        changed();

        return removed;
    }

    @Override
    public DataSet load(PreparedDataSet first, PreparedDataSet... others) {
        List<PreparedDataSet> sets = new ArrayList<>(Arrays.asList(others));
        sets.add(0, first);
        for (PreparedDataSet set : sets) {
            DefaultDataSetMapping dataSetMapping = new DefaultDataSetMapping(set.getSelfType(), clients);
            first.executeOn(dataSetMapping);
            loadInternal(dataSetMapping);
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

    /**
     * Signals that data sets was changed outside the dataset.
     * @return self
     */
    @Override
    public DataSet changed() {
        for (DataSetMappingDefinition.OnChange onChange : onChangeListeners) {
            onChange.doOnChange(this);
        }
        return this;
    }

    @Override
    public DataSet load(Class<?> self, Consumer<DataSetMappingDefinition> configuration) {
        return load(new PreparedDataSet(self, configuration));
    }

    DataSet load(DataSetMapping mapping) {
        loadInternal(mapping);
        return loaded();
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
        onChangeListeners.addAll(mapping.getOnChangeListeners());
    }

    private Client findClient(Class type) {
        Client result = null;
        for (Client client: clients) {
            if (client.isSupported(type)) {
                result = client;
                break;
            }
        }
        return Preconditions.checkNotNull(result, "Client not found for " + type);
    }
}
