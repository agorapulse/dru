package com.agorapulse.dru

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import groovy.transform.PackageScope

/**
 * Missing property report reports properties from the source which hasn't been map to any persistent property
 * so the information may be lost.
 *
 * You can print the report using #toString() method.
 */
class MissingPropertiesReport {

    private static final String DOUBLE_BAR = "\u2016"

    private final Multimap<MissingProperty, MissingProperty> missingProperties = MultimapBuilder
            .treeKeys(
                { MissingProperty a, MissingProperty b ->
                    if (a.type == b.type) {
                        if (a.propertyName == b.propertyName) {
                            return a.path <=> b.path
                        }
                        return a.propertyName <=> b.propertyName
                    }
                    return a.type.simpleName <=> b.type.simpleName
                } as Comparator<MissingProperty>
            )
            .arrayListValues()
            .build()

    @PackageScope void add(MissingProperty missingProperty) {
        missingProperties.put(missingProperty, missingProperty)
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

        missingProperties.values().each {
            pathWidth = Math.max(pathWidth, (it.path ?: '').size())
            typeWidth = Math.max(typeWidth, (it.type.simpleName ?: '').size())
            valueWidth = Math.max(valueWidth, (it.value?.toString() ?: '').size())
            propertyWidth = Math.max(propertyWidth, (it.propertyName ?: '').size())
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
            missingProperties.asMap().values().each { Collection<MissingProperty> missing ->
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
