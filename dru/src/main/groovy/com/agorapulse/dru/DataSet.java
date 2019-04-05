package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

import java.util.List;
import java.util.function.Consumer;

public interface DataSet  {

    <T> T findByTypeAndOriginalId(Class<T> type, Object id);
    <T> List<T> findAllByType(Class<T> type);
    <T> T findByType(Class<T> type);
    <T> T add(T entity);
    <T> T add(T entity, Object manualId);
    <T> T remove(T object);

    /**
     * Loads additional prepared data set into current data set and returns self.
     * @param first additional prepared data set
     * @param rest other additional prepared data sets
     * @return self with items loaded from another prepared data sets
     */
    DataSet load(PreparedDataSet first, PreparedDataSet... rest);

    /**
     * Loads additional data set mapping into current data set and returns self.
     * @param configuration inline configuration closure
     * @return self with items loaded from another data set mapping from the closure
     */
    default DataSet load(
        @DelegatesTo(value = DataSetMappingDefinition.class, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType.class, options = "com.agorapulse.dru.DataSetMappingDefinition")
            Closure<DataSetMappingDefinition> configuration) {
        Object self = configuration.getThisObject();
        if (!(self instanceof Class)) {
            self = self.getClass();
        }

        return load((Class<?>) self, ConsumerWithDelegate.create(configuration));
    }

    /**
     * Loads additional data set mapping into current data set and returns self.
     * @param self the parent object to be used as reference for loading resources
     * @param configuration inline configuration closure
     * @return self with items loaded from another data set mapping from the closure
     */
    DataSet load(Class<?> self, Consumer<DataSetMappingDefinition> configuration);

    /**
     * Signals that data sets was manually loaded into this data set using {@link #add(Object)} or the
     * data has been changed significantly so the attached {@link DataSetMappingDefinition.WhenLoaded} listeners
     * should be notified.
     * @return self
     */
    DataSet loaded();

    /**
     * Signals that data set has been changed significantly so the attached {@link DataSetMappingDefinition.OnChange} listeners
     * should be notified.
     * @return self
     */
    DataSet changed();

    /**
     * Get the report of properties which wasn't handled. This can help you to mine more information from your sources.
     * @return the report of properties which wasn't handled
     */
    MissingPropertiesReport getReport();

}
