package avl

@SuppressWarnings([
    'GrailsDomainHasEquals',
    'GrailsDomainHasToString',
    'ClassJavadoc',
])
/**
 * The person is representation of individual related to the agency.
 */
class Person {

    String name
    String bio

    static constraints = {
        bio nullable: true
    }
}
