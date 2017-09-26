package com.agorapulse.dru

import com.agorapulse.dru.persistence.Client
import com.agorapulse.dru.persistence.Clients
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Data Reconstruction Utility
 */
class Dru implements TestRule, DataSet {

    static Dru plan(@DelegatesTo(value = DataSetMappingDefinition, strategy = Closure.DELEGATE_FIRST) Closure<DataSetMappingDefinition> configuration) {
        return new Dru(configuration.thisObject, new PreparedDataSet(configuration))
    }

    static Dru steal(Object unitTest) {
        return new Dru(unitTest, null)
    }

    static PreparedDataSet prepare(
        @DelegatesTo(value = DataSetMappingDefinition, strategy = Closure.DELEGATE_FIRST)
        Closure<DataSetMappingDefinition> configuration
    ) {
        return new PreparedDataSet(configuration)
    }

    private final Object unitTest
    private final PreparedDataSet preparedDataSet
    private final Set<Client> clients

    private DataSet currentDataSet

    Dru(Object unitTest, PreparedDataSet preparedDataSet) {
        this.unitTest = unitTest
        this.preparedDataSet = preparedDataSet
        this.clients = Clients.createClients(unitTest)
    }

    @Override
    Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                base.evaluate()
                currentDataSet = null
            }
        }
    }
        /**
     * Persist given sources into underlying system and create new data set holding all created items.
     * @return new data set containing all created entities
     */
    DataSet load() {
        if (currentDataSet != null) {
            return currentDataSet
        }

        if (!preparedDataSet) {
            return currentDataSet = new DefaultDataSet(clients)
        }

        DefaultDataSetMapping dataSetMapping = new DefaultDataSetMapping(clients)
        preparedDataSet.executeOn(dataSetMapping)

        return currentDataSet = new DefaultDataSet(clients).load(dataSetMapping)
    }

    DataSet load(PreparedDataSet first, PreparedDataSet... rest) {
        ensureDataSetInitialized().load(first, rest)
    }

    @Override
    DataSet load(
            @DelegatesTo(value = DataSetMappingDefinition, strategy = Closure.DELEGATE_FIRST)
            Closure<DataSetMappingDefinition> configuration) {
        ensureDataSetInitialized().load(configuration)
    }

    @Override
    DataSet loaded() {
        ensureDataSetInitialized().loaded()
    }

    @Override
    <T> T findByTypeAndOriginalId(Class<T> type, Object id) {
        ensureDataSetInitialized().findByTypeAndOriginalId(type, id)
    }

    @Override
    <T> List<T> findAllByType(Class<T> type) {
        return ensureDataSetInitialized().findAllByType(type)
    }

    @Override
    <T> T findByType(Class<T> type) {
        return ensureDataSetInitialized().findByType(type)
    }

    @Override
    <T> T add(T entity) {
        return ensureDataSetInitialized().add(entity)
    }

    @Override
    <T> T add(T entity, Object manualId) {
        return ensureDataSetInitialized().add(entity, manualId)
    }

    @Override
    <T> T remove(T object) {
        ensureDataSetInitialized().remove(object)
    }

    @Override
    MissingPropertiesReport getReport() {
        return ensureDataSetInitialized().report
    }

    private DataSet ensureDataSetInitialized() {
        if (!currentDataSet) {
            load()
        }
        return currentDataSet
    }
}
