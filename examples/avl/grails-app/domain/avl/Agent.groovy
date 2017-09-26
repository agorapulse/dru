package avl

// tag::header[]
class Agent extends Person implements WithSecurityLevel {
// end::header[]

    // tag::properties[]
    String name
    String bio

    Long securityLevel

    static hasOne = [manager: Agent]

    static constraints = {
        securityLevel nullable: false
        bio nullable: true
    }
    // end::properties[]


    static hasMany = [assignments: Assignment, staff: Agent]

    static transients = ['novice']
    List<String> characteristics

    boolean isNovice() {
        securityLevel < 5
    }

// tag::footer[]
}
// end::footer[]
