package avl

class Assignment {

    Agent agent
    Mission mission

    static constraints = {
        agent unique: 'mission'
    }

}
