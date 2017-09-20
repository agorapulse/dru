package com.agorapulse.dru;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.List;

public interface DataSet  {

    <T> T findByTypeAndOriginalId(Class<T> type, Object id);
    <T> List<T> findAllByType(Class<T> type);
    <T> T findByType(Class<T> type);
    <T> T add(Class<T> type, Object id, T entity);
    <T> void remove(Class<T> type, Object id);

    /**
     * Loads additional prepared data set into current data set and returns self.
     * @param first additional prepared data set
     * @param rest other additional prepared data sets
     * @return self with items loaded from another prepared data sets
     */
    DataSet load(PreparedDataSet first, PreparedDataSet... rest);

    /**
     * Loads additional data set mapping into current data set and returns self.
     * @param first additional prepared data set
     * @param rest other additional prepared data sets
     * @return self with items loaded from another data set mappings
     */
    DataSet load(DataSetMapping first, DataSetMapping... rest);

    /**
     * Loads additional data set mapping into current data set and returns self.
     * @param configuration inline configuration closure
     * @return self with items loaded from another data set mapping from the closure
     */
    DataSet load(@DelegatesTo(value = DataSetMappingDefinition.class, strategy = Closure.DELEGATE_FIRST) Closure<DataSetMappingDefinition> configuration);

    /**
     * Signals that data sets was manually loaded into this data set using {@link #add(Class, Object, Object)} or the
     * data has been changed significantly so the attached {@link DataSetMappingDefinition.WhenLoaded} listeners
     * should be notified.
     * @return self
     */
    DataSet loaded();

    /**
     * Get the report of properties which wasn't handled. This can help you to mine more information from your sources.
     * @return the report of properties which wasn't handled
     */
    MissingPropertiesReport getReport();

}
