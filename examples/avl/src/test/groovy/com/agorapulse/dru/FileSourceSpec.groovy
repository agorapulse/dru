package com.agorapulse.dru

import avl.Item
import groovy.json.JsonOutput
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileSourceSpec extends Specification {

    @Rule TemporaryFolder tmp


    void 'load from file'() {
        given:
            File fixture = tmp.newFile('items.json')
            fixture.text = JsonOutput.toJson(new Item(id: 'Id', name: 'Name', description: 'Description'))
            Dru dru = Dru.steal(this)
            dru.load {
                from fixture, {
                    map { to Item }
                }
            }
        expect:
            dru.findAllByType(Item).size() == 1
            new FileSource(this, fixture).toString().startsWith('FileSource')
    }

}
