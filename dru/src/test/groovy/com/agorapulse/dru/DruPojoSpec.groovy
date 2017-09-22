package com.agorapulse.dru

import org.junit.Rule
import spock.lang.Specification

/**
 * Some tests to test Dru with POJOs.
 */
class DruPojoSpec extends Specification {

    @Rule Dru dru = Dru.steal(this)

    void 'load by reflection'() {
        when:
            dru.load {
                from ('LIBRARY') {
                    map {
                        to (Library) {
                            map ('authors') {
                                to Author
                            }
                            map ('genres') {
                                to Genre
                            }
                            map ('books') {
                                to (Book)
                            }
                        }
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
            ],
            [
                title: 'It',
                genres: [
                    [name: 'horror']
                ],
                authors: [
                    [fullName: 'Stephen King']
                ]
            ],
            [
                title: 'The Mist',
                genres: [
                    [name: 'horror']
                ],
                authors: [
                    [fullName: 'Stephen King']
                ]
            ],
            [
                title: 'The Cider House Rules',
                genres: [
                    [name: 'fiction']
                ],
                authors: [
                    [fullName: 'John Irving']
                ]
            ],
        ]
    ]

}
