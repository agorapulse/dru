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
package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.Condition
import groovy.transform.PackageScope

import java.util.function.Predicate

/**
 * Helper class to handle DynamoDB conditions.
 */
@PackageScope class DynamoDBConditions {

    static Predicate conditionToPredicate(Condition condition) {
        Predicate le = {
            it <= getValue(condition.attributeValueList.first())
        }
        Predicate ge = {
            it >= getValue(condition.attributeValueList.first())
        }
        Predicate contains = {
            evaluateContains(it, condition)
        }
        switch (condition.comparisonOperator) {
            case 'EQ': return {
                it == getValue(condition.attributeValueList.first())
            }
            case 'NE': return {
                it != getValue(condition.attributeValueList.first())
            }
            case 'LE': return le
            case 'LT': return {
                it <  getValue(condition.attributeValueList.first())
            }
            case 'GE': return ge
            case 'GT': return {
                it >  getValue(condition.attributeValueList.first())
            }
            case 'NOT_NULL': return {
                it != null
            }
            case 'NULL': return {
                it == null
            }
            case 'CONTAINS': return contains
            case 'NOT_CONTAINS': return {
                !contains.test(it)
            }
            case 'BEGINS_WITH': return {
                it != null && it.toString().startsWith(getValue(condition.attributeValueList.first()).toString())
            }
            case 'IN': return {
                condition.attributeValueList.collectMany { getValue(it) }.contains(it)
            }
            case 'BETWEEN': return {
                it >= getValue(condition.attributeValueList[0]) && it <= getValue(condition.attributeValueList[1])
            }
        }
        throw new IllegalArgumentException("Unknown operator $condition.comparisonOperator")
    }

    @SuppressWarnings('UnnecessaryGetter')
    static Object getValue(AttributeValue value) {
        if (!value) {
            return null
        }
        if (value.isNULL()) {
            return null
        }
        if (value.isBOOL()) {
            return value.getBOOL()
        }
        if (value.getS()) {
            return value.getS()
        }
        if (value.getN()) {
            return new BigDecimal(value.getN())
        }
        if (value.getB()) {
            return value.getB()
        }
        if (value.getSS()) {
            return value.getSS()
        }
        if (value.getNS()) {
            return value.getNS().collect { new BigDecimal(it) }
        }
        if (value.getBS()) {
            return value.getBS()
        }
        if (value.getM()) {
            return value.getM().collectEntries { [it.key, getValue(it.value)] }
        }
        if (value.getL()) {
            return value.getL().collect { getValue(it) }
        }
        return null
    }

    @SuppressWarnings('Instanceof')
    private static boolean evaluateContains(Object value, Condition condition) {
        Object shouldBeContained = getValue(condition.attributeValueList.first())
        if (value instanceof CharSequence) {
            return value.toString().contains(shouldBeContained.toString())
        }
        if (value instanceof Iterable) {
            return value.any { it == shouldBeContained }
        }
        return false
    }
}
