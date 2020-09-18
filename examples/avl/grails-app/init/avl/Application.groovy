package avl

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.context.ConfigurableApplicationContext

/**
 * Entry point to the application.
 */
class Application extends GrailsAutoConfiguration {
    // for testing only
    private static ConfigurableApplicationContext context

    static void main(String[] args) {
        context = GrailsApp.run(Application, args)
    }
}
