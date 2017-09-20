package com.agorapulse.dru.dynamodb.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.model.ScanResult
import groovy.transform.PackageScope

import static DruDynamoDBMapper.getTableNameUsingConfig
import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.PaginationLoadingStrategy.LAZY_LOADING

@PackageScope class SimplePaginatedScanList<T> extends PaginatedScanList<T> {

    SimplePaginatedScanList(DruDynamoDBMapper mapper, Class<T> clazz, List<T> items, DynamoDBMapperConfig config) {
        super(mapper, clazz, null, new ScanRequest(getTableNameUsingConfig(clazz, config)), new ScanResult().withItems([:]), LAZY_LOADING, config)
        allResults.addAll(items)
        allResultsLoaded = true
    }



    @Override
    protected boolean atEndOfResults() {
        true
    }

    @Override
    protected synchronized List<T> fetchNextPage() {
        return Collections.emptyList()
    }

    @Override
    synchronized void loadAllResults() {
        // do nothing
    }
}
