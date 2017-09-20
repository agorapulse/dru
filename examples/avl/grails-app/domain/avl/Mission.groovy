package avl

class Mission {

    String title
    Villain villain

    String description

    Set<String> itemIds = []

    static hasMany = [assignments: Assignment]

    static constraints = { }

}
