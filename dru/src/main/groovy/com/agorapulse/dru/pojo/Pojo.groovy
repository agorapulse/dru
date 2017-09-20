package com.agorapulse.dru.pojo

import com.agorapulse.dru.parser.Parser
import com.agorapulse.dru.persistence.AbstractCacheableClient
import com.agorapulse.dru.persistence.Client
import com.agorapulse.dru.persistence.ClientFactory
import com.agorapulse.dru.persistence.meta.ClassMetadata
import com.agorapulse.dru.persistence.meta.PropertyMetadata
import com.agorapulse.dru.pojo.meta.PojoClassMetadata
import com.google.common.primitives.Primitives

/**
 * Pojo client handles Plain Old Java Objects.
 *
 * This is the default client if there is no other client is available.
 */
class Pojo extends AbstractCacheableClient {

    public static final Pojo INSTANCE = new Pojo()

    static class Factory implements ClientFactory {
        final int index = Integer.MAX_VALUE

        @Override
        boolean isSupported(Object unitTest) {
            return true
        }

        @Override
        Client newClient(Object unitTest) {
            return INSTANCE
        }
    }

    @Override
    protected boolean computeIsSupported(Class type) {
        return type.declaredConstructors.find { it.parameters.size() == 0 } || type == Map
    }

    @Override
    <T> T save(T object) {
        return object
    }

    @Override
    @SuppressWarnings('CatchException')
    <T> T newInstance(Parser parser, Class<T> type, Map<String, Object> payload) {
        if (type == Map) {
            return new LinkedHashMap<>(payload) as T
        }
        try {
            return type.newInstance(payload)
        } catch (ClassCastException e) {
            payload.each {
                if (it.value != null) {
                    Class expectedType = getClassMetadata(type).getPersistentProperty(it.key).type
                    if (!Primitives.wrap(expectedType).isAssignableFrom(it.value.getClass())) {
                        throw new IllegalArgumentException("Wrong type for property $it.key: expected $expectedType, got ${it.value.getClass()}.", e)
                    }
                }
            }
            throw new IllegalArgumentException("Failed to create new instance of $type with payload: $payload", e)
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create new instance of $type with payload: $payload", e)
        }
    }

    @Override
    <T> T addTo(T object, String association, Object other) {
        if (object == null) {
            return object
        }

        PropertyMetadata metadata = getClassMetadata(object.class).getPersistentProperty(association)

        if (!metadata) {
            throw new IllegalArgumentException("Unknown association: $association of $object")
        }

        if (!Collection.isAssignableFrom(metadata.type)) {
            throw new UnsupportedOperationException("Cannot add to $metadata.type")
        }

        if (object[association] == null) {
            object[association] = createCollection(metadata.type)
        }

        object[association].add(other)

        return object
    }

    protected ClassMetadata createClassMetadata(Class type) {
        new PojoClassMetadata(type)
    }

    private static Collection createCollection(Class type) {
        if (type.interface) {
            switch (type) {
                case Collection: return []
                case List: return []
                case Set: return new LinkedHashSet()
                case Queue: return [] as Queue
                case SortedSet: return [] as SortedSet
                case NavigableSet: return [] as SortedSet
            }
        }
        return type.newInstance() as Collection
    }
}
