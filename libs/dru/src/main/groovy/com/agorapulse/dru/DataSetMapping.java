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

import java.util.List;
import java.util.Map;
import java.util.Set;

interface DataSetMapping {

    Map<String, Source> getSources();

    List<DataSetMappingDefinition.WhenLoaded> getWhenLoadedListeners();
    List<DataSetMappingDefinition.OnChange> getOnChangeListeners();

    Set<Client> getClients();

    TypeMappings getTypeMappings();

    void applyOverrides(Class type, Object destination, Object source);

    void applyDefaults(Class<?> type, Object destination, Object source);

    boolean isIgnored(Class<?> type, String propertyName);

}
