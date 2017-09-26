package com.agorapulse.dru

import com.agorapulse.dru.parser.Parser
import com.agorapulse.dru.persistence.meta.CachedClassMetadata
import com.agorapulse.dru.persistence.meta.CachedPropertyMetadata
import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import com.agorapulse.dru.pojo.Pojo
import com.agorapulse.dru.pojo.meta.PojoClassMetadata
import com.agorapulse.dru.pojo.meta.PojoPropertyMetadata
import org.junit.Rule
import spock.lang.Specification

/**
 * Some tests to test Dru with POJOs.
 */
class DruPojoSpec extends Specification {

    @Rule Dru dru = Dru.steal(this)

    void 'guess associations'() {
        when:
            PojoPropertyMetadata authors = new PojoPropertyMetadata(Library, 'authors', true)
        then:
            authors.referencedPropertyType == Author
            authors.type == SortedSet
            authors.association
            authors.persistent
            authors.referencedPropertyName == null

            // cannot decide so far
            authors.oneToMany
            authors.manyToMany

            !authors.manyToOne
            !authors.oneToOne
            !authors.embedded
    }

    void 'load by reflection'() {
        when:
            dru.load {
                from ('LIBRARY') {
                    map {
                        to Library
                    }
                }
                any (Author) {
                    overrides {
                        id = it.fullName
                    }
                }

                any (Book) {
                    map ('authors') {
                        to Author
                    }
                    map ('genres') {
                        to Genre
                    }
                    overrides {
                        id = it.title
                    }
                }
            }
        then:
            dru.findByType(Library)
            !dru.findByType(String)
            dru.findAllByType(Book).size() == 4
        when:
            dru.remove('foo')
        then:
            noExceptionThrown()

    }

    void 'Pojo new instance'() {
        given:
            Parser parser = Mock(Parser)
        expect:
            Pojo.INSTANCE.newInstance(parser, Map, [foo: 'bar']) == [foo: 'bar']
        when:
            PojoTester tester = Pojo.INSTANCE.newInstance(parser, PojoTester, [booleanValue: true])
        then:
            tester.booleanValue
        when:
            Pojo.INSTANCE.newInstance(parser, PojoTester, [numericalValue: '123fooBAR'])
        then:
            IllegalArgumentException iae = thrown(IllegalArgumentException)
            iae.message == 'Wrong type for property numericalValue: expected int, got class java.lang.String.'
        when:
            Pojo.INSTANCE.newInstance(parser, PojoTester, [numericalValue: null])
        then:
            IllegalArgumentException iae2 = thrown(IllegalArgumentException)
            iae2.message == 'Failed to create new instance of class com.agorapulse.dru.PojoTester with payload: [numericalValue:null]'
        when:
            Pojo.INSTANCE.newInstance(parser, PojoTester.ThrowsExceptionInConstructor, [:])
        then:
            IllegalArgumentException iae3 = thrown(IllegalArgumentException)
            iae3.message == 'Failed to create new instance of class com.agorapulse.dru.PojoTester$ThrowsExceptionInConstructor with payload: [:]'
        when:
            Pojo.INSTANCE.newInstance(parser, PojoTester, [willThrowClassCastException: 'foo'])
        then:
            IllegalArgumentException iae4 = thrown(IllegalArgumentException)
            iae4.message == 'Failed to create new instance of class com.agorapulse.dru.PojoTester with payload: [willThrowClassCastException:foo]'
    }

    void 'Pojo add to'() {
        expect:
            Pojo.INSTANCE.addTo(null, 'foo', 'bar') == null
        when:
            Pojo.INSTANCE.addTo(new PojoTester(), 'foo', 'bar')
        then:
            thrown(IllegalArgumentException)
        when:
            Pojo.INSTANCE.addTo(new PojoTester(), 'numericalValue', 'bar')
        then:
            thrown(UnsupportedOperationException)
        when:
            PojoTester tester = Pojo.INSTANCE.addTo(new PojoTester(), 'collectionValue', 'bar')
        then:
            tester.collectionValue
            tester.collectionValue.contains('bar')
        when:
            PojoTester tester2 = Pojo.INSTANCE.addTo(new PojoTester(), 'collectionSubClassValue', 'bar')
        then:
            tester2.collectionSubClassValue
            tester2.collectionSubClassValue.contains('bar')
    }

    @SuppressWarnings('UnnecessaryObjectReferences')
    void 'cached metadata'() {
        when:
            ClassMetadata classMetadata = new PojoClassMetadata(PojoTester)
            CachedClassMetadata cachedClassMetadata = new CachedClassMetadata(classMetadata)
            Map<String, Object> fixture = [id: 1]
        then:
            classMetadata.getId(fixture) == cachedClassMetadata.getId(fixture)
            classMetadata.type == cachedClassMetadata.type
            classMetadata.persistentProperties*.name == cachedClassMetadata.persistentProperties*.name
            classMetadata == cachedClassMetadata.original
        when:
            PropertyMetadata propertyMetadata = classMetadata.getPersistentProperty('numericalValue')
            CachedPropertyMetadata cachedPropertyMetadata = cachedClassMetadata.getPersistentProperty('numericalValue')
        then:
            propertyMetadata.name == cachedPropertyMetadata.name
            propertyMetadata.type == cachedPropertyMetadata.type
            propertyMetadata.referencedPropertyType == cachedPropertyMetadata.referencedPropertyType
            propertyMetadata.persistent == cachedPropertyMetadata.persistent
            propertyMetadata.oneToMany == cachedPropertyMetadata.oneToMany
            propertyMetadata.manyToOne == cachedPropertyMetadata.manyToOne
            propertyMetadata.manyToMany == cachedPropertyMetadata.manyToMany
            propertyMetadata.oneToOne == cachedPropertyMetadata.oneToOne
            propertyMetadata.association == cachedPropertyMetadata.association
            propertyMetadata.owningSide == cachedPropertyMetadata.owningSide
            propertyMetadata.referencedPropertyName == cachedPropertyMetadata.referencedPropertyName
            propertyMetadata.embedded == cachedPropertyMetadata.embedded
            propertyMetadata.basicCollectionType == cachedPropertyMetadata.basicCollectionType
            propertyMetadata == cachedPropertyMetadata.original
        and:
            classMetadata.getPersistentProperty('collectionValue').referencedPropertyType == String
            classMetadata.getPersistentProperty('collectionSubClassValue').referencedPropertyType == Integer
            classMetadata.getPersistentProperty('rawCollectionValue').referencedPropertyType == Object
            classMetadata.getPersistentProperty('objectCollectionValue').referencedPropertyType == Object
            classMetadata.getPersistentProperty('abstractCollectionWithInterfaceValue').referencedPropertyType == Boolean
    }

    void 'triggers loaded'() {
        when:
            int count = 0
            Dru dru = Dru.steal(this)
            PreparedDataSet whenLoadedDataSet = Dru.prepare {
                whenLoaded {
                    count = count + 1
                }
            }
        then:
            count == 0
        when:
            dru.load(whenLoadedDataSet)
        then:
            count == 1
        when:
            dru.load()
        then:
            count == 1
        when:
            dru.loaded()
        then:
            count == 2
    }

    void 'add item to data set and retrieve it'() {
        when:
            int id = 5
            Dru dru = Dru.steal(this)
            dru.add(new Library(name: 'Library of Congress'), id)
        then:
            dru.findByTypeAndOriginalId(Library, id)
    }

    public static final Map<String, Object> LIBRARY = [
        name: 'National Library',
        authors: [
            [fullName: 'Stephen King'],
            [fullName: 'John Irving'],
            [fullName: 'James S.A. Corey'],
        ],
        genres: [
            [name: 'horror'],
            [name: 'thriller'],
            [name: 'sci-fi'],
            [name: 'fiction'],
            [name: 'romance'],
        ],
        books: [
            [
                title: 'Leviathan Wakes',
                genres: [
                    [name: 'sci-fi']
                ],
                authors: [
                    [fullName: 'James S.A. Corey']
                ],
                ext: [
                    'adapted_as': 'The Expanse'
                ]
            ],
            [
                title: 'It',
                genres: [
                    [name: 'horror']
                ],
                authors: [
                    [fullName: 'Stephen King']
                ],
                tags: ['scary', 'adapted']
            ],
            [
                title: 'The Mist',
                genres: [
                    [name: 'horror']
                ],
                authors: [
                    [fullName: 'Stephen King']
                ],
                tags: ['scary', 'adapted']
            ],
            [
                title: 'The Cider House Rules',
                genres: [
                    [name: 'fiction']
                ],
                authors: [
                    [fullName: 'John Irving']
                ],
                tags: ['adapted']
            ],
        ]
    ]

}
