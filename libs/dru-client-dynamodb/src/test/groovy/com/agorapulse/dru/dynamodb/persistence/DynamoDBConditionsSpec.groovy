package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition

import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Predicate

import static com.amazonaws.services.dynamodbv2.model.ComparisonOperator.*
import static java.nio.ByteBuffer.wrap

/**
 * Testing DynamoDB conditions.
 */
@Unroll @SuppressWarnings('NoWildcardImports')
class DynamoDBConditionsSpec extends Specification {

    void '#description'() {
        when:
            Condition condition = new Condition()
                .withComparisonOperator(operator as ComparisonOperator)
                .withAttributeValueList(attributeValues)
            Predicate predicate = DynamoDBConditions.conditionToPredicate(condition)
        then:
            predicate.test(value) == matches
        where:
            matches | value             | operator      | attributeValues
            true    | 1                 | EQ            | [new AttributeValue().withN('1')]
            false   | 1                 | NE            | [new AttributeValue().withN('1')]
            true    | true              | EQ            | [new AttributeValue().withBOOL(true)]
            false   | true              | EQ            | [new AttributeValue().withBOOL(false)]
            true    | false             | NE            | [new AttributeValue().withBOOL(true)]
            true    | 1                 | BETWEEN       | [new AttributeValue().withN('0'), new AttributeValue().withN('2')]
            true    | 1                 | LE            | [new AttributeValue().withN('1')]
            false   | 1                 | LT            | [new AttributeValue().withN('1')]
            true    | 1                 | LT            | [new AttributeValue().withN('2')]
            true    | 1                 | GE            | [new AttributeValue().withN('1')]
            false   | 1                 | GT            | [new AttributeValue().withN('1')]
            true    | 3                 | GT            | [new AttributeValue().withN('2')]
            false   | 1                 | BETWEEN       | [new AttributeValue().withN('5'), new AttributeValue().withN('10')]
            true    | null              | NULL          | []
            false   | null              | NOT_NULL      | []
            false   | null              | NOT_NULL      | []
            true    | null              | NULL          | []
            true    | 'foo'             | BEGINS_WITH   | [new AttributeValue().withS('f')]
            false   | 'bar'             | BEGINS_WITH   | [new AttributeValue().withS('f')]
            true    | 'foobar'          | CONTAINS      | [new AttributeValue().withS('oba')]
            false   | 'foobar'          | CONTAINS      | [new AttributeValue().withS('baz')]
            false   | 'foobar'          | NOT_CONTAINS  | [new AttributeValue().withS('oba')]
            true    | 'foobar'          | NOT_CONTAINS  | [new AttributeValue().withS('baz')]
            true    | ['foo', 'bar']    | CONTAINS      | [new AttributeValue().withS('foo')]
            true    | ['foo', 'bar']    | NOT_CONTAINS  | [new AttributeValue().withS('baz')]
            true    |  'foo'            | IN            | [new AttributeValue().withSS('foo', 'bar')]
            false   | 999               | CONTAINS      | [new AttributeValue().withS('9')]

            description = "$value $operator ${matches ? 'matches' : 'does not match'} $attributeValues"
    }

    void 'throw exception when operator is FOO'() {
        when:
            DynamoDBConditions.conditionToPredicate(new Condition().withComparisonOperator('FOO'))
        then:
            thrown(IllegalArgumentException)
    }

    void '#attr is paresed to #value'() {
        expect:
            DynamoDBConditions.getValue(attr) == value
        where:
            value                                   | attr
            null                                    | null
            null                                    | new AttributeValue().withNULL(true)
            wrap('foo'.bytes)                       | new AttributeValue().withB(wrap('foo'.bytes))
            [wrap('foo'.bytes), wrap('bar'.bytes)]  | new AttributeValue().withBS(wrap('foo'.bytes), wrap('bar'.bytes))
            1                                       | new AttributeValue().withN('1')
            [1, 2]                                  | new AttributeValue().withNS('1', '2')
            '1'                                     | new AttributeValue().withS('1')
            ['1', '2']                              | new AttributeValue().withSS('1', '2')
            [foo: 'bar']                            | new AttributeValue().withM(foo: new AttributeValue().withS('bar'))
            [1, '2']                                | new AttributeValue().withL(new AttributeValue().withN('1'), new AttributeValue().withS('2'))
    }

}
