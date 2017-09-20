package com.agorapulse.dru.persistence;

import com.agorapulse.dru.persistence.meta.CachedClassMetadata;
import com.agorapulse.dru.persistence.meta.ClassMetadata;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCacheableClient implements Client {

    private final Map<Class, CachedClassMetadata> classMetadata = new HashMap<>();
    private final Map<Class, Boolean> supportedClasses = new HashMap<>();

    @Override
    public final boolean isSupported(Class type) {
        if (supportedClasses.containsKey(type)) {
            return supportedClasses.get(type);
        }
        boolean supported = computeIsSupported(type);
        supportedClasses.put(type, supported);
        return supported;
    }

    @Override
    public final CachedClassMetadata getClassMetadata(Class type) {
        CachedClassMetadata metadata = classMetadata.get(type);
        if (metadata == null) {
            metadata = new CachedClassMetadata(createClassMetadata(type));
            classMetadata.put(type, metadata);
        }
        return metadata;
    }

    protected abstract ClassMetadata createClassMetadata(Class type);
    protected abstract boolean computeIsSupported(Class type);
}
