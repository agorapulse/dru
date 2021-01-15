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
package com.agorapulse.dru.persistence;

import com.agorapulse.dru.parser.Parser;
import com.agorapulse.dru.persistence.meta.ClassMetadata;

import java.util.Map;

public interface Client {

    boolean isSupported(Class type);

    ClassMetadata getClassMetadata(Class type);

    <T> T newInstance(Parser parser, Class<T> type, Map<String, Object> payload);
    <T> T save(T object);
    String getId(Object object);
    String getId(Class type, Map<String, Object> properties);
    <T> T addTo(T object, String association, Object other);

}
