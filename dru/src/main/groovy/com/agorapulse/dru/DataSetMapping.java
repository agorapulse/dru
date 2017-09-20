package com.agorapulse.dru;

import com.agorapulse.dru.persistence.Client;

import java.util.List;
import java.util.Map;
import java.util.Set;

interface DataSetMapping {

    Map<String, Source> getSources();

    List<DataSetMappingDefinition.WhenLoaded> getWhenLoadedListeners();

    Set<Client> getClients();

    TypeMappings getTypeMappings();

    void applyOverrides(Class type, Object destination, Object source);

    void applyDefaults(Class<?> type, Object destination, Object source);

    boolean isIgnored(Class<?> type, String propertyName);

}
