package com.agorapulse.dru.persistence

import com.agorapulse.dru.persistence.meta.CachedClassMetadata
import com.agorapulse.dru.persistence.meta.ClassMetadata

/**
 * Base class for clients with caching capabilities.
 */
abstract class AbstractCacheableClient implements Client {

    private final Map<Class, CachedClassMetadata> classMetadata = [:]
    private final Map<Class, Boolean> supportedClasses = [:]

    @Override
    final boolean isSupported(Class type) {
        if (supportedClasses.containsKey(type)) {
            return supportedClasses.get(type)
        }
        boolean supported = computeIsSupported(type)
        supportedClasses.put(type, supported)
        return supported
    }

    @Override
    final CachedClassMetadata getClassMetadata(Class type) {
        CachedClassMetadata metadata = classMetadata.get(type)
        if (metadata == null) {
            metadata = new CachedClassMetadata(createClassMetadata(type))
            classMetadata.put(type, metadata)
        }
        return metadata
    }

    @Override
    String getId(Object object) {
        return getId(object.getClass(), object.properties)
    }

    @Override
    String getId(Class type, Map<String, Object> properties) {
        ClassMetadata metadata = getClassMetadata(type)
        Object id = metadata.getId(properties)
        return id != null ? String.valueOf(id) : null
    }

    protected abstract ClassMetadata createClassMetadata(Class type)

    protected abstract boolean computeIsSupported(Class type)
}
