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
package com.agorapulse.dru.parser;

import com.agorapulse.dru.Source;

import java.util.Map;

public interface Parser {

    int getIndex();

    boolean isSupported(Source relativePath);

    Object getContent(Source source);

    /**
     * Asks the source to help covert the value to desired type.
     * @param <T> desired type
     * @param path path to the value
     * @param value string representation of object
     * @param desiredType desired type
     * @return object of desired type created based on the string representation
     */
    <T> T convertValue(String path, Object value, Class<T> desiredType);

    Iterable<Map<String, Object>> findAllMatching(Object content, String path);
}
