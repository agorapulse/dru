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
package com.agorapulse.dru.mn.data.jdbc;

import com.agorapulse.dru.mn.data.jdbc.meta.MicronautDataClassMetadata;
import com.agorapulse.dru.parser.Parser;
import com.agorapulse.dru.persistence.AbstractCacheableClient;
import com.agorapulse.dru.persistence.Client;
import com.agorapulse.dru.persistence.ClientFactory;
import com.agorapulse.dru.persistence.meta.ClassMetadata;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextProvider;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.exceptions.IntrospectionException;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.data.exceptions.EmptyResultException;
import io.micronaut.data.model.runtime.RuntimeAssociation;
import io.micronaut.data.model.runtime.RuntimePersistentProperty;
import io.micronaut.data.operations.RepositoryOperations;
import io.micronaut.data.repository.GenericRepository;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.transaction.SynchronousTransactionManager;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;


public class MicronautData extends AbstractCacheableClient implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicronautData.class);
    private static final Set<String> PERSIST_METHOD_NAMES = new HashSet<>(Arrays.asList(
        "save", "persist", "insert", "store"
    ));

    private static final String FIND_BY_ID_METHOD_NAME_PREFIX = "findBy";
    private static final String FIND_BY_ID_METHOD_NAME = FIND_BY_ID_METHOD_NAME_PREFIX + "Id";

    private static final String UPDATE_METHOD_NAME = "update";

    public static class Factory implements ClientFactory {

        private static final int INDEX = 9999;

        @Override
        public int getIndex() {
            return INDEX;
        }

        @Override
        public boolean isSupported(Object unitTest) {
            boolean supported = unitTest instanceof ApplicationContextProvider;
            if (!supported) {
                LOGGER.warn("MicronautData is on the claspath but the unit test class does not implement " +
                    "io.micronaut.context.ApplicationContextProvider. Micronaut Data entities won't be loaded for this test");
            }
            return supported;
        }

        @Override
        public Client newClient(Object unitTest) {
            return new MicronautData(((ApplicationContextProvider) unitTest));
        }
    }

    private final ApplicationContextProvider provider;

    private MicronautData(ApplicationContextProvider provider) {
        this.provider = provider;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected boolean computeIsSupported(Class type) {
        try {
            ops().getEntity(type);
            return true;
        } catch (IntrospectionException e) {
            return false;
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ClassMetadata createClassMetadata(Class type) {
        return new MicronautDataClassMetadata(ops().getEntity(type), type);
    }

    @Override
    public <T> T newInstance(Parser parser, Class<T> type, Map<String, Object> payload) {
        BeanIntrospection<T> introspection = ops().getEntity(type).getIntrospection();
        T instance = introspection.instantiate(
            Arrays.stream(introspection.getConstructorArguments()).map(Argument::getName).map(payload::get).toArray()
        );
        payload.forEach((k, v) -> introspection.getProperty(k).ifPresent(p -> {
            if (!p.isReadOnly()) {
                p.set(instance, v);
            }
        }));
        return instance;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> T save(T object) {
        Class<?> type = object.getClass();
        ApplicationContext ctx = provider.getApplicationContext();

        SynchronousTransactionManager<T> transactionManager = ctx.getBean(SynchronousTransactionManager.class);
        Collection<BeanDefinition<GenericRepository>> definitions = ctx.getBeanDefinitions(GenericRepository.class);

        for (BeanDefinition<GenericRepository> definition : definitions) {
            Optional<ExecutableMethod<GenericRepository, ?>> maybeSaveMethod = tryFindSaveMethod(type, definition);

            if (maybeSaveMethod.isPresent()) {
                GenericRepository repository = ctx.getBean(definition.getBeanType());

                Object id = getOriginalId(object);

                try {
                    if (id != null) {
                        Optional<ExecutableMethod<GenericRepository, ?>> maybeFindByIdMethod = tryFindFindByIdMethod(type, definition);

                        if (!maybeFindByIdMethod.isPresent()) {
                            LOGGER.warn(
                                "Skipping object " + object + " with id " + id + " which is probably already persisted as there is no easy way how to update it. Please, provide 'findById' in " + repository.getClass() + " class: "
                                    + object
                                    + "! If you are missing some information then re-arrange the fixtures so the most complete fixture goes first."
                            );
                            return object;
                        }

                        Object existing = transactionManager.executeRead(s -> maybeFindByIdMethod.get().invoke(repository, getOriginalId(object)));
                        Maybe<T> maybeExists = asMaybeEntity(existing, object);
                        if (!maybeExists.isEmpty().blockingGet()) {
                            Optional<ExecutableMethod<GenericRepository, ?>> maybeUpdateMethod = tryFindUpdateMethod(type, definition);
                            if (maybeUpdateMethod.isPresent()) {
                                T updated = asMaybeEntity(transactionManager.executeWrite(s -> maybeUpdateMethod.get().invoke(repository, object)), object)
                                    .doOnError(th -> LOGGER.error("Exception saving object asynchronously " + object, th))
                                    .blockingGet(object);
                                LOGGER.debug("Updated object " + object + " as " + updated);
                                return updated;
                            }
                            LOGGER.warn(
                                "Skipping object " + object + " with id " + id + " which is probably already persisted as there is no easy way how to update it.Please, provide 'update' method in  " + repository.getClass() + "  class: "
                                    + object
                                    + "! If you are missing some information then re-arrange the fixtures so the most complete fixture goes first."
                            );
                            return object;
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn(
                        "Skipping object  " + object + " with id " + id + " which is probably already persisted as there were issues during the update! " +
                            "If you are missing some information then re-arrange the fixtures so the most complete fixture goes first.",
                        e
                    );
                    return object;
                }

                try {
                    ExecutableMethod<GenericRepository, ?> saveMethod = maybeSaveMethod.get();
                    if (!definition.findAnnotation("io.micronaut.data.jdbc.annotation.JdbcRepository").isPresent()) {
                        // JPA does not support id set on a new object
                        removeId(object);
                    }
                    Object result = transactionManager.executeWrite(s -> saveMethod.invoke(repository, object));
                    T saved = asMaybeEntity(result, object)
                        .doOnError(th -> LOGGER.error("Exception saving object asynchronously " + object, th))
                        .blockingGet(object);
                    LOGGER.debug("Saved object " + object + " as " + saved);
                    return saved;
                } catch (Exception e) {
                    LOGGER.error("Exception saving object " + object, e);
                    throw e;
                }
            }
        }

        throw new IllegalStateException(
            "Cannot find method to save " + object + ". Please, create a repository for type " + type
        );
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public String getId(Class type, Map<String, Object> properties) {
        return getIdName(type)
            .flatMap(idName -> Optional.ofNullable(properties.get(idName)))
            .map(String::valueOf)
            .orElse(null);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T addTo(T object, String association, Object other) {
        ops().getEntity(object.getClass()).getPersistentProperties()
            .stream()
            .filter(p -> p.getName().equals(association))
            .filter(p -> p instanceof RuntimeAssociation)
            .findFirst()
            .ifPresent(p -> {
                BeanProperty<T, ?> property = (BeanProperty<T, ?>) p.getProperty();
                Object collection = property.get(object);
                if (collection instanceof Collection) {
                    Collection coll = (Collection) collection;
                    coll.add(other);
                }
            });

        return object;
    }

    private RepositoryOperations ops() {
        return provider.getApplicationContext().getBean(RepositoryOperations.class);
    }

    @SuppressWarnings("unchecked")
    private <T> Maybe<T> asMaybeEntity(Object result, T original) {
        if (original.getClass().isInstance(result)) {
            return Maybe.just((T) result);
        }
        if (result instanceof Optional) {
            Optional<T> optional = (Optional<T>) result;
            return optional.map(Maybe::just).orElseGet(Maybe::empty);
        }
        if (result instanceof CompletableFuture) {
            return Maybe.create(emitter -> {
                CompletableFuture<T> future = (CompletableFuture<T>) result;
                try {
                    emitter.onSuccess(future.get());
                    emitter.onComplete();
                } catch (Throwable th) {
                    if (th.getCause() instanceof EmptyResultException) {
                        emitter.onComplete();
                    } else {
                        emitter.onError(th);
                    }
                }
            });
        }
        if (result instanceof Single) {
            return Maybe.fromSingle((Single<T>) result);
        }
        if (result instanceof Maybe) {
            return (Maybe<T>) result;
        }
        if (result instanceof Publisher) {
            Publisher<T> publisher = (Publisher<T>) result;
            return Maybe.create(emitter -> publisher.subscribe(new Subscriber<T>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(1);
                }

                @Override
                public void onNext(T t) {
                    emitter.onSuccess(t);
                }

                @Override
                public void onError(Throwable t) {
                    emitter.onError(t);
                }

                @Override
                public void onComplete() {
                    emitter.onComplete();
                }
            }));
        }

        LOGGER.warn("Cannot cast object " + result + " to Maybe<" + original.getClass() + ">");

        return Maybe.empty();
    }

    @SuppressWarnings("unchecked")
    private <T> Object getOriginalId(T object) {
        BeanIntrospection<T> introspection = (BeanIntrospection<T>) ops().getEntity(object.getClass()).getIntrospection();
        return getIdName(object.getClass())
            .flatMap(introspection::getProperty)
            .map(p -> p.get(object))
            .orElse(null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Optional<String> getIdName(Class type) {
        RuntimePersistentProperty identity = ops().getEntity(type).getIdentity();
        if (identity == null) {
            return Optional.empty();
        }

        return Optional.of(identity.getName());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void removeId(Object object) {
        RuntimePersistentProperty identity = ops().getEntity(object.getClass()).getIdentity();
        if (identity == null) {
            return;
        }

        BeanProperty property = identity.getProperty();
        if (!property.isReadOnly()) {
            property.set(object, null);
        }
    }

    @SuppressWarnings("rawtypes")
    private Optional<ExecutableMethod<GenericRepository, ?>> tryFindFindByIdMethod(Class<?> type, BeanDefinition<GenericRepository> definition) {
        return tryFindMethod(type, definition, m ->
            FIND_BY_ID_METHOD_NAME.equals(m.getMethodName())
                || getIdName(type).map(name -> (FIND_BY_ID_METHOD_NAME_PREFIX + StringUtils.capitalize(name)).equals(m.getMethodName())).orElse(false)
        );
    }

    @SuppressWarnings("rawtypes")
    private Optional<ExecutableMethod<GenericRepository, ?>> tryFindSaveMethod(Class<?> type, BeanDefinition<GenericRepository> definition) {
        return tryFindMethod(type, definition, m ->
            PERSIST_METHOD_NAMES.stream().anyMatch(prefix -> m.getMethodName().startsWith(prefix))
                && m.getArgumentTypes().length == 1
                && m.getArgumentTypes()[0].equals(type)
        );
    }

    @SuppressWarnings("rawtypes")
    private Optional<ExecutableMethod<GenericRepository, ?>> tryFindUpdateMethod(Class<?> type, BeanDefinition<GenericRepository> definition) {
        return tryFindMethod(type, definition, m ->
            UPDATE_METHOD_NAME.equals(m.getMethodName())
                && m.getArgumentTypes().length == 1
                && m.getArgumentTypes()[0].equals(type)
        );
    }

    @SuppressWarnings("rawtypes")
    private Optional<ExecutableMethod<GenericRepository, ?>> tryFindMethod(Class<?> type, BeanDefinition<GenericRepository> definition, Predicate<? super ExecutableMethod<GenericRepository, ?>> filter) {
        return definition.getExecutableMethods().stream().filter(filter).findFirst();
    }

}
