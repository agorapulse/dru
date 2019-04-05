package com.agorapulse.dru;

import java.util.function.Consumer;

public class PreparedDataSet {

    private final Class<?> selfType;
    private final Consumer<DataSetMappingDefinition> dataSetDefinition;

    PreparedDataSet(Class<?> selfType, Consumer<DataSetMappingDefinition> dataSetDefinition) {
        this.selfType = selfType;
        this.dataSetDefinition = dataSetDefinition;
    }

    void executeOn(DataSetMappingDefinition dataSet) {
        dataSetDefinition.accept(dataSet);
    }

    public Class<?> getSelfType() {
        return selfType;
    }
}
