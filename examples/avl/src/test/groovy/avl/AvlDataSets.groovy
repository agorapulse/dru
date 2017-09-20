package avl

import com.agorapulse.dru.Dru
import com.agorapulse.dru.PreparedDataSet

class AvlDataSets {

    static final PreparedDataSet missions = Dru.prepare {
        from ('missions.json') {
            map ('missions') {
                to (Mission) {
                    map ('items') {
                        to (new: Item)
                    }
                }
            }
        }
    }

}
