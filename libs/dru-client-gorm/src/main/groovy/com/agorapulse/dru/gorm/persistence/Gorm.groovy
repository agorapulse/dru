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
package com.agorapulse.dru.gorm.persistence

import com.agorapulse.dru.parser.Parser
import com.agorapulse.dru.gorm.persistence.meta.GormClassMetadata
import com.agorapulse.dru.persistence.AbstractCacheableClient
import com.agorapulse.dru.persistence.Client
import com.agorapulse.dru.persistence.ClientFactory
import com.agorapulse.dru.persistence.meta.ClassMetadata
import grails.testing.gorm.DataTest
import org.grails.datastore.gorm.GormEnhancer
import org.grails.datastore.gorm.GormEntity

/**
 * Client for GORM.
 */
class Gorm extends AbstractCacheableClient {

    static class Factory implements ClientFactory {
        final int index = 10000

        @Override
        @SuppressWarnings([
            'Instanceof',
            'LineLength',
            'SystemErrPrint',
        ])
        boolean isSupported(Object unitTest) {
            return true
        }

        @Override
        Client newClient(Object unitTest) {
            return new Gorm(unitTest in DataTest ? unitTest as DataTest : null)
        }
    }

    private final DataTest dataTest
    private final Set<Class> mockedDomainClasses = new LinkedHashSet<>()

    private Gorm(DataTest dataTest) {
        this.dataTest = dataTest
    }

    @Override
    boolean computeIsSupported(Class type) {
        return GormEntity.isAssignableFrom(type)
    }

    @Override
    protected ClassMetadata createClassMetadata(Class type) {
        ensureMocked(type)
        return new GormClassMetadata(GormEnhancer.findStaticApi(type).gormPersistentEntity)
    }

    @Override
    <T> T save(T object) {
        return object.save(failOnError: true)
    }

    @Override
    <T> T addTo(T object, String association, Object other) {
        return object.addTo(association, other)
    }

    @Override
    <T> T newInstance(Parser parser, Class<T> type, Map<String, Object> payload) {
        return type.newInstance(payload)
    }

    void ensureMocked(Class domainClass) {
        if (dataTest == null) {
            return
        }
        if (!mockedDomainClasses.contains(domainClass)) {
            mockedDomainClasses.add(domainClass)
            dataTest.mockDomain(domainClass)
        }
    }
}
