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
package dru.micronaut.example.jpa

import com.agorapulse.dru.Dru
import dru.micronaut.example.jpa.Book
import dru.micronaut.example.jpa.BookRepository
import dru.micronaut.example.jpa.Manufacturer
import dru.micronaut.example.jpa.ManufacturerRepository
import dru.micronaut.example.jpa.OwnerRepository
import dru.micronaut.example.jpa.Pet
import dru.micronaut.example.jpa.PetRepository
import dru.micronaut.example.jpa.Product
import dru.micronaut.example.jpa.ProductRepository
import dru.micronaut.example.jpa.Sale
import dru.micronaut.example.jpa.SaleRepository
import groovy.sql.Sql
import io.micronaut.context.ApplicationContext
import io.micronaut.context.ApplicationContextProvider
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.transaction.SynchronousTransactionManager
import io.reactivex.Flowable
import spock.lang.Specification

import javax.inject.Inject
import javax.sql.DataSource

@MicronautTest
class MicronautDataJpaSpec extends Specification implements ApplicationContextProvider {

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
            id    : 345,
            name  : 'Rocket',
            type  : 'CAT',
            owner : [
                id  : 123,
                name: 'Susan',
                age : 8,
            ],
        ],
        [
            id    : 789,
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

    @SuppressWarnings(['GroovyAssignabilityCheck', 'unused'])
    private static final List<Map<String, Object>> MANUFACTURERS_SUMMARY = [
        [
            id: 100,
            name: 'APPLE',
            products: [
                [
                    id: 12345,
                    name: 'iPhone 13',
                ],
                [
                    id: 12346,
                    name: 'iPhone 13 Mini',
                    sales: [
                        [
                            quantity: 5,
                        ],
                        [
                            quantity: 6,
                        ],
                    ]
                ],
            ]
        ]
    ]

    @Inject ApplicationContext applicationContext
    @Inject BookRepository bookRepository
    @Inject OwnerRepository ownerRepository
    @Inject PetRepository petRepository
    @Inject ManufacturerRepository manufacturerRepository
    @Inject ProductRepository productRepository
    @Inject SaleRepository saleRepository

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

    void 'load from a different side'() {
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
