package avl

import com.agorapulse.dru.dynamodb.persistence.DruDynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import grails.boot.GrailsApp
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

import javax.servlet.ServletContext

class SanitySpec extends Specification {

    @ConfineMetaClassChanges(GrailsApp)
    void 'mock run app'() {
        when:
            Application.main()
        then:
            Application.context
        when:
            Application.context.stop()
        then:
            noExceptionThrown()
    }

    void 'some stupid calls'() {
        given:
            ServletContext context = Mock(ServletContext)
        expect:
            !new BootStrap().init(context)
            !new BootStrap().destroy()
    }


    void 'test get table name'() {
        expect:
            DruDynamoDBMapper.getTableNameUsingConfig(Item, DynamoDBMapperConfig.builder().build()) == 'Item'
    }

    void 'new marshaller test'() {
        when:
            new ExtMarshaller()
        then:
            noExceptionThrown()
    }

}
