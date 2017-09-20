package avl

import com.agorapulse.dru.Dru
import com.agorapulse.dru.dynamodb.persistence.DynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import grails.testing.gorm.DataTest
import org.junit.Rule
import spock.lang.Specification

class AvlDataSetsSpec extends Specification implements DataTest {

    @Rule Dru dru = Dru.steal(this)

    void 'load assignments'() {
        when:
            dru.load(AvlDataSets.missions)
            println dru.report
        then:
            dru.findAllByType(Mission).size() == 1
            dru.findAllByType(Agent).size() == 2
            dru.findAllByType(Assignment).size() == 2
            dru.findAllByType(Villain).size() == 1
            dru.findAllByType(Item).size() == 1
        and:
            Mission.list().size() == 1
            Agent.list().size() == 2
            Assignment.list().size() == 2
            Villain.list().size() == 1
        when:
            DynamoDBMapper mapper = DynamoDB.createMapper(dru)
        then:
            mapper.count(Item.class, new DynamoDBQueryExpression<Item>()) == 1
    }

}
