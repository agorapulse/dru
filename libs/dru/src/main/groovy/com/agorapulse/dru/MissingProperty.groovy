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
package com.agorapulse.dru

/**
 * Missing property is a missing property from the source which hasn't found its counterpart in any persistent property
 * and because of that the information it is holding is lost.
 *
 * Use TypeMapping#ignore() to explicitly declare property as one which can be ignored.
 */
class MissingProperty {

    static MissingProperty create(String path, String propertyName, Object value, Class type) {
        return new MissingProperty(path, propertyName, value, type)
    }

    final String path
    final String propertyName
    final Object value
    final Class type

    private MissingProperty(String path, String propertyName, Object value, Class type) {
        this.path = path
        this.propertyName = propertyName
        this.value = value
        this.type = type
    }
}
