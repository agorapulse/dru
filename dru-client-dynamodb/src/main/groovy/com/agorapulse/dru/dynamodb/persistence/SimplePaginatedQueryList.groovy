package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.amazonaws.services.dynamodbv2.model.QueryResult
import groovy.transform.PackageScope

import static DruDynamoDBMapper.getTableNameUsingConfig
import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.PaginationLoadingStrategy.LAZY_LOADING

/**
 * List based SimplePaginatedQueryList.
 * @param <T> type of the items
 */
@PackageScope class SimplePaginatedQueryList<T> extends PaginatedQueryList<T> {

    SimplePaginatedQueryList(DruDynamoDBMapper mapper, Class<T> clazz, List<T> items, DynamoDBMapperConfig config) {
        super(mapper, clazz, null, new QueryRequest(getTableNameUsingConfig(clazz, config)), new QueryResult().withItems([:]), LAZY_LOADING, config)
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
