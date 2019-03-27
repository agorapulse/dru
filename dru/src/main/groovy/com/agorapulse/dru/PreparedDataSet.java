package com.agorapulse.dru;

import java.util.function.Consumer;

public class PreparedDataSet {

    private final Consumer<DataSetMappingDefinition> dataSetDefinition;

    PreparedDataSet(Consumer<DataSetMappingDefinition> dataSetDefinition) {
        this.dataSetDefinition = dataSetDefinition;
    }

    void executeOn(DataSetMappingDefinition dataSet) {
        dataSetDefinition.accept(dataSet);
    }

}
