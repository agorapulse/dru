package avl

class Agent extends Person implements WithSecurityLevel {

    Long securityLevel

    static hasMany = [assignments: Assignment]

    static transients = ['novice']

    boolean isNovice() {
        securityLevel < 5
    }
    static constraints = {
        securityLevel nullable: false
        bio nullable: true
    }

}
