package avl

class Mission {

    String title
    Villain villain

    String description

    static hasMany = [assignments: Assignment]

    static constraints = {
        villain nullable: true
        description nullable: true
    }

}
