package avl

@SuppressWarnings([
    'GrailsDomainHasEquals',
    'GrailsDomainHasToString',
    'ClassJavadoc',
])
/**
 * The assignment of the agent to particular mission.
 */
class Assignment {

    Agent agent
    Mission mission

    static constraints = {
        agent unique: 'mission'
    }

}
