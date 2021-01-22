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

import com.agorapulse.dru.persistence.Client;
import com.agorapulse.dru.persistence.Clients;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Data Reconstruction Utility
 */
public class Dru implements TestRule, DataSet, Closeable {

    public static Dru create(Object unitTest, Consumer<DataSetMappingDefinition> configuration) {
        Object self = unitTest;
        if (!(self instanceof Class)) {
            self = self.getClass();
        }
        return new Dru(unitTest, new PreparedDataSet((Class<?>) self, configuration));
    }

    public static Dru create(@DelegatesTo(value = DataSetMappingDefinition.class, strategy = Closure.DELEGATE_FIRST) Closure<DataSetMappingDefinition> configuration) {
        return create(configuration.getThisObject(), ConsumerWithDelegate.create(configuration));
    }

    public static Dru create(Object unitTest) {
        return new Dru(unitTest, null);
    }

    /**
     * @deprecated use {@link #create(Object, Consumer) instead}
     */
    @Deprecated
    public static Dru plan(Object unitTest, Consumer<DataSetMappingDefinition> configuration) {
        return create(unitTest, configuration);
    }

    /**
     * @deprecated use {@link #create(Closure) instead}
     */
    @Deprecated
    public static Dru plan(@DelegatesTo(value = DataSetMappingDefinition.class, strategy = Closure.DELEGATE_FIRST) Closure<DataSetMappingDefinition> configuration) {
        return plan(configuration.getThisObject(), ConsumerWithDelegate.create(configuration));
    }

    /**
     * @deprecated use {@link #create(Object) instead}
     */
    @Deprecated
    public static Dru steal(Object unitTest) {
        return new Dru(unitTest, null);
    }

    public static PreparedDataSet prepare(Class<?> self, Consumer<DataSetMappingDefinition> configuration) {
        return new PreparedDataSet(self, configuration);
    }

    public static PreparedDataSet prepare(@DelegatesTo(value = DataSetMappingDefinition.class, strategy = Closure.DELEGATE_FIRST) Closure<DataSetMappingDefinition> configuration) {
        Object self = configuration.getThisObject();
        if (!(self instanceof Class)) {
            self = self.getClass();
        }
        return prepare((Class<?>) self, ConsumerWithDelegate.create(configuration));
    }

    public Dru(Object unitTest, PreparedDataSet preparedDataSet) {
        this.unitTest = unitTest;
        this.preparedDataSet = preparedDataSet;
        this.clients = new LinkedHashSet<>(Clients.createClients(unitTest));
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                close();
            }

        };
    }

    /**
     * Persist given sources into underlying system and create new data set holding all created items.
     *
     * @return new data set containing all created entities
     */
    public DataSet load() {
        if (currentDataSet != null) {
            return currentDataSet;
        }


        if (preparedDataSet == null) {
            return currentDataSet = new DefaultDataSet(unitTest, clients);
        }


        DefaultDataSetMapping dataSetMapping = new DefaultDataSetMapping(unitTest, clients);
        preparedDataSet.executeOn(dataSetMapping);

        return currentDataSet = new DefaultDataSet(unitTest, clients).load(dataSetMapping);
    }

    public DataSet load(PreparedDataSet first, PreparedDataSet... rest) {
        return ensureDataSetInitialized().load(first, rest);
    }

    @Override
    public DataSet load(Class<?> self, Consumer<DataSetMappingDefinition> configuration) {
        return ensureDataSetInitialized().load(self, configuration);
    }

    @Override
    public DataSet loaded() {
        return ensureDataSetInitialized().loaded();
    }

    @Override
    public DataSet changed() {
        return ensureDataSetInitialized().changed();
    }

    @Override
    public <T> T findByTypeAndOriginalId(Class<T> type, Object id) {
        return ensureDataSetInitialized().findByTypeAndOriginalId(type, id);
    }

    @Override
    public <T> List<T> findAllByType(Class<T> type) {
        return ensureDataSetInitialized().findAllByType(type);
    }

    @Override
    public <T> T findByType(Class<T> type) {
        return ensureDataSetInitialized().findByType(type);
    }

    @Override
    public <T> T add(T entity) {
        return ensureDataSetInitialized().add(entity);
    }

    @Override
    public <T> T add(T entity, Object manualId) {
        return ensureDataSetInitialized().add(entity, manualId);
    }

    @Override
    public <T> T remove(T object) {
        return ensureDataSetInitialized().remove(object);
    }

    @Override
    public MissingPropertiesReport getReport() {
        return ensureDataSetInitialized().getReport();
    }

    @Override
    public void close() {
        currentDataSet = null;
    }

    private DataSet ensureDataSetInitialized() {
        if (currentDataSet == null) {
            load();
        }

        return currentDataSet;
    }

    private final Object unitTest;
    private final PreparedDataSet preparedDataSet;
    private final Set<Client> clients;
    private DataSet currentDataSet;
}
