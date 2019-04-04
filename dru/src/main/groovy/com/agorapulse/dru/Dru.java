package com.agorapulse.dru;

import com.agorapulse.dru.persistence.Client;
import com.agorapulse.dru.persistence.Clients;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Data Reconstruction Utility
 */
public class Dru implements TestRule, DataSet {

    public static Dru plan(Object unitTest, Consumer<DataSetMappingDefinition> configuration) {
        Object self = unitTest;
        if (!(self instanceof Class)) {
            self = self.getClass();
        }
        return new Dru(unitTest, new PreparedDataSet((Class<?>) self, configuration));
    }

    public static Dru plan(@DelegatesTo(value = DataSetMappingDefinition.class, strategy = Closure.DELEGATE_FIRST) Closure<DataSetMappingDefinition> configuration) {
        return plan(configuration.getThisObject(), ConsumerWithDelegate.create(configuration));
    }

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
                currentDataSet = null;
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
