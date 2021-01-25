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
package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.model.ScanResult
import groovy.transform.PackageScope

import static DruDynamoDBMapper.getTableNameUsingConfig
import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.PaginationLoadingStrategy.LAZY_LOADING

/**
 * List based SimplePaginatedScanList.
 * @param <T>
 */
@PackageScope class SimplePaginatedScanList<T> extends PaginatedScanList<T> {

    SimplePaginatedScanList(DruDynamoDBMapper mapper, Class<T> clazz, List<T> items, DynamoDBMapperConfig config) {
        super(mapper, clazz, null, new ScanRequest(getTableNameUsingConfig(clazz, config)), new ScanResult(), LAZY_LOADING, config)
        allResults.addAll(items)
        allResultsLoaded = true
    }

    @Override
    protected boolean atEndOfResults() {
        true
    }

    @Override
    @SuppressWarnings('SynchronizedMethod')
    protected synchronized List<T> fetchNextPage() {
        return Collections.emptyList()
    }

    @Override
    @SuppressWarnings('SynchronizedMethod')
    synchronized void loadAllResults() {
        // do nothing
    }
}
