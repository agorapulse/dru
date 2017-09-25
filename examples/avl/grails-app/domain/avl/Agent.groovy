package avl

class Agent extends Person implements WithSecurityLevel {

    Long securityLevel

    List<String> characteristics

    static hasMany = [assignments: Assignment, staff: Agent]
    static hasOne = [manager: Agent]

    static transients = ['novice']

    boolean isNovice() {
        securityLevel < 5
    }
    static constraints = {
        securityLevel nullable: false
        bio nullable: true
    }

}
