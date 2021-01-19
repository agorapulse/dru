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
import io.micronaut.data.model.runtime.RuntimeAssociation;
import io.micronaut.data.model.runtime.RuntimePersistentProperty;
import io.micronaut.data.operations.RepositoryOperations;
import io.micronaut.data.repository.GenericRepository;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.transaction.SynchronousTransactionManager;
import io.reactivex.Single;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class MicronautData extends AbstractCacheableClient implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicronautData.class);
    private static final Set<String> PERSIST_METHOD_NAMES = new HashSet<>(Arrays.asList(
        "save", "persist", "insert", "store"
    ));

    public static class Factory implements ClientFactory {

        private static final int INDEX = 9999;

        @Override
        public int getIndex() {
            return INDEX;
        }

        @Override
        public boolean isSupported(Object unitTest) {
            return unitTest instanceof ApplicationContextProvider;
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
        return introspection.instantiate(
            Arrays.stream(introspection.getConstructorArguments()).map(Argument::getName).map(payload::get).toArray()
        );
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> T save(T object) {
        ApplicationContext ctx = provider.getApplicationContext();

        SynchronousTransactionManager<T> transactionManager = ctx.getBean(SynchronousTransactionManager.class);

        Collection<BeanDefinition<GenericRepository>> definitions = ctx.getBeanDefinitions(GenericRepository.class);

        for (BeanDefinition<GenericRepository> definition : definitions) {
            Optional<ExecutableMethod<GenericRepository, ?>> potentialSaveMethod = definition.getExecutableMethods().stream().filter(m ->
                PERSIST_METHOD_NAMES.stream().anyMatch(prefix -> m.getMethodName().startsWith(prefix))
                    && m.getArgumentTypes().length == 1
                    && m.getArgumentTypes()[0].equals(object.getClass())
            ).findFirst();

            if (potentialSaveMethod.isPresent()) {
                ExecutableMethod<GenericRepository, ?> saveMethod = potentialSaveMethod.get();
                GenericRepository repository = ctx.getBean(definition.getBeanType());
                try {
                    if (getId(object) != null) {
                        LOGGER.warn(
                            "Skipping already presisted object: "
                                + object
                                + "! If you are missing some information then re-arrange the fixtures so the most complete fixture goes first."
                        );
                        return object;
                    }
                    Object result = transactionManager.executeWrite(s -> saveMethod.invoke(repository, object));
                    return asEntity(result, object);
                } catch (Exception e) {
                    LOGGER.error("Exception saving object " + object, e);
                    throw e;
                }
            }
        }

        throw new IllegalStateException(
            "Cannot find method to save " + object + ". Please, create a repository for type " + object.getClass()
        );
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String getId(Class type, Map<String, Object> properties) {
        RuntimePersistentProperty identity = ops().getEntity(type).getIdentity();
        if (identity == null) {
            return null;
        }

        Object id = properties.get(identity.getName());

        if (id == null) {
            return null;
        }

        return String.valueOf(id);
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
    private <T> T asEntity(Object result, T original) {
        if (original.getClass().isInstance(result)) {
            return (T) result;
        }
        if (result instanceof CompletableFuture) {
            try {
                return ((CompletableFuture<T>) result).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IllegalStateException("Exception during asynchronous save of " + original, e);
            }
        }
        if (result instanceof Single) {
            return ((Single<T>) result).blockingGet();
        }
        if (result instanceof Publisher) {
            return Single.fromPublisher((Publisher<T>) result).blockingGet();
        }

        LOGGER.warn("Cannot cast object " + result + " to " + original.getClass());

        return original;
    }
}
