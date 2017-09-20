package com.agorapulse.dru;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class PreparedDataSet {

    private final Closure<DataSetMappingDefinition> dataSetDefinitionClosure;

    PreparedDataSet(Closure<DataSetMappingDefinition> dataSetDefinitionClosure) {
        this.dataSetDefinitionClosure = dataSetDefinitionClosure;
    }

    void executeOn(DataSetMappingDefinition dataSet) {
        DefaultGroovyMethods.with(dataSet, dataSetDefinitionClosure);
    }

}
