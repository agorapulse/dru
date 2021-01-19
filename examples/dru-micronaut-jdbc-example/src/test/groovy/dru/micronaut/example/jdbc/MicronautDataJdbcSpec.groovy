/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dru.micronaut.example.jdbc

import com.agorapulse.dru.Dru
import io.micronaut.context.ApplicationContext
import io.micronaut.context.ApplicationContextProvider
import io.micronaut.test.annotation.MicronautTest
import io.reactivex.Flowable
import spock.lang.Specification

import javax.inject.Inject
import javax.sql.DataSource

@MicronautTest
class MicronautDataJdbcSpec extends Specification implements ApplicationContextProvider {

    @SuppressWarnings(['GroovyAssignabilityCheck', 'unused'])
    private static final List<Map<String, Object>> BOOKS = [
        [
            id    : 12345,
            title : 'It',
            pages : 1116,
            author: [id: 666],
        ],
        [
            id    : 12666,
            title : 'The Shining',
            pages : 659,
            author: [id: 666],
        ],
    ]

    @SuppressWarnings(['GroovyAssignabilityCheck', 'unused'])
    private static final List<Map<String, Object>> PETS = [
        [
            id    : '3980c10c-973f-4a17-98be-7a091553d802',
            name  : 'Rocket',
            type  : 'CAT',
            owner : [
                id  : 123,
                name: 'Susan',
                age : 8,
            ],
        ],
        [
            id    : 'f09e223f-00aa-4503-a2a5-fc10137bd7d2',
            name  : 'Mickey',
            type  : 'DOG',
            owner : [
                id  : 123,
                name: 'Susan',
                age : 8,
            ],
        ],
    ]

    @SuppressWarnings(['GroovyAssignabilityCheck', 'unused'])
    private static final List<Map<String, Object>> MANUFACTURERS = [
        [
            id: 100,
            name: 'APPLE',
        ]
    ]

    @SuppressWarnings(['GroovyAssignabilityCheck', 'unused'])
    private static final List<Map<String, Object>> PRODUCTS = [
        [
            id: 12345,
            name: 'iPhone 13',
            manufacturer: [id: 100],
        ],
        [
            id: 12346,
            name: 'iPhone 13 Mini',
            manufacturer: [id: 100],
        ],
    ]

    @SuppressWarnings(['GroovyAssignabilityCheck', 'unused'])
    private static final List<Map<String, Object>> SALES = [
        [
            product: [id: 12345],
            quantity: 5,
        ],
        [
            product: [id: 12346],
            quantity: 6,
        ],
    ]

    @Inject ApplicationContext applicationContext
    @Inject BookRepository bookRepository
    @Inject OwnerRepository ownerRepository
    @Inject PetRepository petRepository
    @Inject ManufacturerRepository manufacturerRepository
    @Inject ProductRepository productRepository
    @Inject SaleRepository saleRepository
    @Inject DataSource dataSource

    void 'load books'() {
        given:
            Dru.plan {
                from 'BOOKS', {
                    map {
                        to Book
                    }
                }
            }.load()
        expect:
            bookRepository.count() == 2
    }

    void 'load pets'() {
        given:
            Dru.plan {
                from 'PETS', {
                    map {
                        to Pet
                    }
                }
            }.load()
        expect:
            verifyAll {
                petRepository.count() == 2
                ownerRepository.count() == 1
            }
    }

    void 'load sales with references'() {
        given:
            Dru.plan {
                from 'MANUFACTURERS', {
                    map {
                        to Manufacturer
                    }
                }
                from 'PRODUCTS', {
                    map {
                        to Product
                    }
                }
                from 'SALES', {
                    map {
                        to Sale
                    }
                }
            }.load()
        expect:
            verifyAll {
                Flowable.fromPublisher(manufacturerRepository.count()).blockingFirst() == 1
                productRepository.count().get() == 2
                saleRepository.count().blockingGet() == 2
            }
    }

}
