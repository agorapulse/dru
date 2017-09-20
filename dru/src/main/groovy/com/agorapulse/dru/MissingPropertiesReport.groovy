package com.agorapulse.dru

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import groovy.transform.PackageScope

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

    /**
     * This reports is truthy if there were no properties missing model.
     * @return true if there were no properties missing in the domain model
     */
    boolean asBoolean() {
        missingProperties.isEmpty()
    }

    @Override
    String toString() {
        final int MINIMAL_WIDTH = 8
        final int pathWidth = MINIMAL_WIDTH
        final int typeWidth = MINIMAL_WIDTH
        final int valueWidth = MINIMAL_WIDTH
        final int propertyWidth = MINIMAL_WIDTH

        missingProperties.values().each {
            pathWidth = Math.max(pathWidth, (it.path ?: '').size())
            typeWidth = Math.max(typeWidth, (it.type.simpleName ?: '').size())
            valueWidth = Math.max(valueWidth, (it.value?.toString() ?: '').size())
            propertyWidth = Math.max(propertyWidth, (it.propertyName ?: '').size())
        }

        StringWriter stringWriter = new StringWriter()
        PrintWriter writer = new PrintWriter(stringWriter)
        String headline = "$DOUBLE_BAR ${'TYPE'.padRight(typeWidth)} $DOUBLE_BAR ${'PROPERTY'.padRight(propertyWidth)} $DOUBLE_BAR ${'PATH'.padRight(pathWidth)} $DOUBLE_BAR ${'VALUE'.padRight(valueWidth)} $DOUBLE_BAR"

        int lineWidth = headline.size()

        String divider = "=" * lineWidth

        writer.println(divider)
        writer.println headline
        writer.println(divider)
        if (!missingProperties.isEmpty()) {
            missingProperties.asMap().values().each { Collection<MissingProperty> missing ->
                MissingProperty toPrint = missing.find { it.value }
                if (!toPrint) {
                    toPrint = missing.first()
                }
                writer.println "$DOUBLE_BAR ${toPrint.type.simpleName.padRight(typeWidth)} | ${toPrint.propertyName.padRight(propertyWidth)} | ${toPrint.path.padRight(pathWidth)} | ${(toPrint.value != null ? toPrint.value.toString() : '<null>').padRight(valueWidth)} $DOUBLE_BAR"
            }
        } else {
            writer.println "$DOUBLE_BAR${' '.center(lineWidth - 2)}$DOUBLE_BAR"
            writer.println "$DOUBLE_BAR ${'DRU SAYS: GOOD JOB, NOTHING IGNORED'.center(lineWidth - 4)} $DOUBLE_BAR"
            writer.println "$DOUBLE_BAR${' '.center(lineWidth - 2)}$DOUBLE_BAR"
        }

        writer.println(divider)
        return stringWriter.toString()
    }

}
