package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.Condition
import groovy.transform.PackageScope

import java.nio.ByteBuffer

@PackageScope class DynamoDBConditions {

    static Closure<Boolean> conditionToClosure(Condition condition) {
        Closure<Boolean> le = {
            it <= getValue(condition.attributeValueList.first())
        }
        Closure<Boolean> ge = {
            it >= getValue(condition.attributeValueList.first())
        }
        Closure<Boolean> contains = {
            evaluateContains(it, condition)
        }
        switch (condition.comparisonOperator) {
            case "EQ": return {
                it == getValue(condition.attributeValueList.first())
            }
            case "NE": return {
                it != getValue(condition.attributeValueList.first())
            }
            case "LE": return le
            case "LT": return {
                it <  getValue(condition.attributeValueList.first())
            }
            case "GE": return ge
            case "GT": return {
                it >  getValue(condition.attributeValueList.first())
            }
            case "NOT_NULL": return {
                it != null
            }
            case "NULL": return {
                it == null
            }
            case "CONTAINS": return contains
            case "NOT_CONTAINS": return {
                !contains(it)
            }
            case "BEGINS_WITH": return {
                it != null && it.toString().startsWith(getValue(condition.attributeValueList.first()).toString())
            }
            case "IN": return {
                condition.attributeValueList.collect { getValue(it) }.contains(it)
            }
            case "BETWEEN": return {
                it >= getValue(condition.attributeValueList[0]) && it <= getValue(condition.attributeValueList[1])
            }
        }
        return { false }
    }

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

    static AttributeValue toValue(Object o) {
        if (o == null) {
            return new AttributeValue().withNULL(true)
        }
        switch(o) {
            case CharSequence: return new AttributeValue().withS(o.toString())
            case Number: return new AttributeValue().withN(o.toString())
            case Boolean: return new AttributeValue().withBOOL(o as Boolean)
            case Iterable: return getValueForIterable(o as Iterable)
            case Map: return new AttributeValue().withM((o as Map).collectEntries { [it.key, toValue(it.value)]} as Map<String, AttributeValue>)
            case Serializable: return new AttributeValue().withB(toByteBuffer(o))
        }
        throw new UnsupportedOperationException("Cannot convert object $o to AttributeValue")
    }

    static ByteBuffer toByteBuffer(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        baos.withStream { stream ->
            ObjectOutput out = new ObjectOutputStream(stream)
            out.withStream {
                it.writeObject(o)
            }
        }

        return ByteBuffer.wrap(baos.toByteArray())
    }
}
