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

import groovy.transform.PackageScope

/**
 * Missing property report reports properties from the source which hasn't been map to any persistent property
 * so the information may be lost.
 *
 * You can print the report using #toString() method.
 */
class MissingPropertiesReport {

    private static final String DOUBLE_BAR = "\u2016"
    private static final String EMPTY_STRING = ''

    private static final Comparator<MissingProperty> MISSING_PROPERTY_COMPARATOR = { MissingProperty a, MissingProperty b ->
        if (a.type == b.type) {
            if (a.propertyName == b.propertyName) {
                return a.path <=> b.path
            }
            return a.propertyName <=> b.propertyName
        }
        return a.type.simpleName <=> b.type.simpleName
    } as Comparator<MissingProperty>

    private final Map<MissingProperty, List<MissingProperty>> missingProperties = new TreeMap<>(MISSING_PROPERTY_COMPARATOR)

    @PackageScope void add(MissingProperty missingProperty) {
        missingProperties.computeIfAbsent(missingProperty) { new ArrayList<>() }.add(missingProperty)
    }

    boolean isEmpty() {
        return missingProperties.isEmpty()
    }

    @Override
    String toString() {
        final int MINIMAL_WIDTH = 8
        int pathWidth = MINIMAL_WIDTH
        int typeWidth = MINIMAL_WIDTH
        int valueWidth = MINIMAL_WIDTH
        int propertyWidth = MINIMAL_WIDTH

        missingProperties.collectMany { it.value }.each {
            pathWidth = Math.max(pathWidth, (it.path ?: EMPTY_STRING).size())
            typeWidth = Math.max(typeWidth, (it.type.simpleName ?: EMPTY_STRING).size())
            valueWidth = Math.max(valueWidth, (it.value?.toString() ?: EMPTY_STRING).size())
            propertyWidth = Math.max(propertyWidth, (it.propertyName ?: EMPTY_STRING).size())
        }

        return writeReport(typeWidth, propertyWidth, pathWidth, valueWidth).toString()
    }

    @SuppressWarnings(['LineLength', 'DuplicateNumberLiteral', 'DuplicateStringLiteral'])
    private StringWriter writeReport(int typeWidth, int propertyWidth, int pathWidth, int valueWidth) {
        StringWriter stringWriter = new StringWriter()

        PrintWriter writer = new PrintWriter(stringWriter)
        String headline = "$DOUBLE_BAR ${'TYPE'.padRight(typeWidth)} $DOUBLE_BAR ${'PROPERTY'.padRight(propertyWidth)} $DOUBLE_BAR ${'PATH'.padRight(pathWidth)} $DOUBLE_BAR ${'VALUE'.padRight(valueWidth)} $DOUBLE_BAR"

        int lineWidth = headline.size()

        String divider = '=' * lineWidth

        writer.println(divider)
        writer.println headline
        writer.println(divider)

        if (missingProperties.isEmpty()) {
            writeOkMessage(writer, lineWidth)
        } else {
            missingProperties.values().each { List<MissingProperty> missing ->
                MissingProperty toPrint = missing.find { it.value } ?: missing.first()
                writer.println "$DOUBLE_BAR ${toPrint.type.simpleName.padRight(typeWidth)} | ${toPrint.propertyName.padRight(propertyWidth)} | ${toPrint.path.padRight(pathWidth)} | ${(toPrint.value != null ? toPrint.value.toString() : '<null>').padRight(valueWidth)} $DOUBLE_BAR"
            }
        }

        writer.println(divider)
        stringWriter
    }

    @SuppressWarnings(['DuplicateNumberLiteral', 'DuplicateStringLiteral'])
    private static void writeOkMessage(PrintWriter writer, int lineWidth) {
        writer.println "$DOUBLE_BAR${' '.center(lineWidth - 2)}$DOUBLE_BAR"
        writer.println "$DOUBLE_BAR ${'DRU SAYS: GOOD JOB, NOTHING IGNORED'.center(lineWidth - 4)} $DOUBLE_BAR"
        writer.println "$DOUBLE_BAR${' '.center(lineWidth - 2)}$DOUBLE_BAR"
    }

}
