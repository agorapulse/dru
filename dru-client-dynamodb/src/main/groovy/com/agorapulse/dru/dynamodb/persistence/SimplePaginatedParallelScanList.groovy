package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedParallelScanList
import com.amazonaws.services.dynamodbv2.datamodeling.ParallelScanTask
import groovy.transform.PackageScope

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.PaginationLoadingStrategy.LAZY_LOADING

/**
 * List based SimplePaginatedParallelScanList.
 * @param <T> type of the items
 */
@PackageScope class SimplePaginatedParallelScanList<T> extends PaginatedParallelScanList<T> {

    SimplePaginatedParallelScanList(DruDynamoDBMapper mapper, Class<T> clazz, List<T> items, DynamoDBMapperConfig config) {
        super(mapper, clazz, null, new ParallelScanTask(mapper, null, []), LAZY_LOADING, config)
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
