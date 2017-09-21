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
            dru.findAllByType(Mission).size() == 2
            dru.findAllByType(Agent).size() == 3
            dru.findAllByType(Assignment).size() == 4
            dru.findAllByType(Villain).size() == 2
            dru.findAllByType(Item).size() == 2
        and:
            Mission.list().size() == 2
            Agent.list().size() == 3
            Assignment.list().size() == 4
            Villain.list().size() == 2
        when:
            DynamoDBMapper mapper = DynamoDB.createMapper(dru)
        then:
            mapper.count(Item.class, new DynamoDBQueryExpression<Item>()) == 2
            mapper.load(new Item(name: "Dupont Diamond"))
            mapper.load(Item.class, "Dupont Diamond")
        when:
            Map<String, List<Object>> batchFetch = mapper.batchLoad([new Item(name: "Dupont Diamond")])
        then:
            batchFetch.size() == 1
            batchFetch['Item']
            batchFetch['Item'].size() == 1
            batchFetch['Item'][0].name == "Dupont Diamond"
    }

}
