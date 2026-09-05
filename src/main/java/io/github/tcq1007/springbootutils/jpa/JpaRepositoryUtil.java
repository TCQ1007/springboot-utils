package io.github.tcq1007.springbootutils.jpa;

import org.springframework.data.util.Lazy;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.support.DefaultRepositoryInvokerFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@SuppressWarnings("unchecked")
@Component
@ConditionalOnClass({ JpaRepository.class, EntityManager.class })
public class JpaRepositoryUtil implements ApplicationContextAware {

    private static final Map<Class<?>, JpaRepository<?, ?>> repositoryCache = new ConcurrentHashMap<>();
    private static Repositories repositories;
    private static ApplicationContext applicationContext;
    private static final Lazy<EntityManager> entityManager = Lazy.of(() -> applicationContext.getBean(EntityManager.class));
    private static DefaultRepositoryInvokerFactory repositoryInvokerFactory;

    public static RepositoryInvoker getRepositoryInvoker(Class<?> domainClass) {
        checkRepositories();
        return repositoryInvokerFactory.getInvokerFor(domainClass);
    }

    private static void checkRepositories() {
        if (repositories == null) {
            throw new IllegalStateException("JpaRepositoryUtil not properly initialized. "
                    + "Please ensure this bean is managed by Spring and ApplicationContext is set.");
        }
    }

    public static <T, K> SimpleJpaRepository<T, K> getSimpleJpaRepository(Class<T> domainClass) {
        checkRepositories();
        return (SimpleJpaRepository<T, K>) repositories.getRepositoryFor(domainClass).orElse(null);
    }

    /**
     * 鏍规嵁瀹炰綋绫昏幏鍙栧搴旂殑 Repository锛堥渶涓?{@link JpaRepository}锛夈€?     */
    public static <E, ID> JpaRepository<E, ID> getRepository(Class<E> entityClass) {
        checkRepositories();

        if (!repositories.hasRepositoryFor(entityClass)) {
            throw new IllegalArgumentException(
                    String.format("No repository found for entity class: %s. "
                                    + "Please ensure this class is a valid JPA entity and has a corresponding repository.",
                            entityClass.getName())
            );
        }

        return (JpaRepository<E, ID>) repositoryCache.computeIfAbsent(entityClass, clazz -> {
            Optional<Object> repositoryOpt = repositories.getRepositoryFor(entityClass);
            return repositoryOpt
                    .map(repo -> (JpaRepository<?, ?>) repo)
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("Repository for entity class %s exists but cannot be retrieved.",
                                    entityClass.getName())
                    ));
        });
    }

    public static <E, ID> Optional<JpaRepository<E, ID>> getRepositoryOptional(Class<E> entityClass) {
        try {
            return Optional.of(getRepository(entityClass));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static boolean isEntityClass(Class<?> clazz) {
        if (repositories == null) {
            return false;
        }
        return repositories.hasRepositoryFor(clazz);
    }

    public static void clearCache() {
        repositoryCache.clear();
    }

    public static EntityManager entityManager() {
        return entityManager.get();
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        JpaRepositoryUtil.applicationContext = applicationContext;
        JpaRepositoryUtil.repositories = new Repositories(applicationContext);
        JpaRepositoryUtil.repositoryInvokerFactory = new DefaultRepositoryInvokerFactory(repositories);
    }
}
