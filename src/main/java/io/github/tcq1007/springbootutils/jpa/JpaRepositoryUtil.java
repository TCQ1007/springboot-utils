package io.github.tcq1007.springbootutils.jpa;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import nl.talsmasoftware.lazy4j.Lazy;
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

    private static final Map<Class<?>, JpaRepository<?, ?>> REPOSITORY_CACHE = new ConcurrentHashMap<>();

    private static Repositories repositories;
    private static ApplicationContext applicationContext;
    private static DefaultRepositoryInvokerFactory repositoryInvokerFactory;
    private static final Lazy<EntityManager> ENTITY_MANAGER =
            Lazy.of(() -> requireContext().getBean(EntityManager.class));

    public static RepositoryInvoker getRepositoryInvoker(Class<?> domainClass) {
        requireRepositories();
        return repositoryInvokerFactory.getInvokerFor(domainClass);
    }

    public static <T, K> SimpleJpaRepository<T, K> getSimpleJpaRepository(Class<T> domainClass) {
        requireRepositories();
        return (SimpleJpaRepository<T, K>) repositories.getRepositoryFor(domainClass).orElse(null);
    }

    /**
     * 根据实体类获取对应的 Repository（需为 {@link JpaRepository}）。
     */
    public static <E, ID> JpaRepository<E, ID> getRepository(Class<E> entityClass) {
        requireRepositories();
        return (JpaRepository<E, ID>) REPOSITORY_CACHE.computeIfAbsent(entityClass, clazz -> {
            Object repository = repositories.getRepositoryFor(clazz).orElseThrow(() ->
                    new IllegalArgumentException(
                            "No repository found for entity class: " + clazz.getName()));
            if (!(repository instanceof JpaRepository<?, ?> jpaRepository)) {
                throw new IllegalArgumentException(
                        "Repository for entity class " + clazz.getName() + " is not a JpaRepository");
            }
            log.debug("cached repository {} for {}", repository.getClass().getName(), clazz.getName());
            return jpaRepository;
        });
    }

    public static <E, ID> Optional<JpaRepository<E, ID>> getRepositoryOptional(Class<E> entityClass) {
        try {
            return Optional.of(getRepository(entityClass));
        } catch (IllegalArgumentException e) {
            log.debug("repository not found for {}", entityClass.getName());
            return Optional.empty();
        }
    }

    public static boolean isEntityClass(Class<?> clazz) {
        return repositories != null && repositories.hasRepositoryFor(clazz);
    }

    public static void clearCache() {
        REPOSITORY_CACHE.clear();
        log.debug("repository cache cleared");
    }

    public static EntityManager entityManager() {
        return ENTITY_MANAGER.get();
    }

    private static Repositories requireRepositories() {
        if (repositories == null) {
            throw new IllegalStateException("JpaRepositoryUtil is not initialized");
        }
        return repositories;
    }

    private static ApplicationContext requireContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("JpaRepositoryUtil is not initialized");
        }
        return applicationContext;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        JpaRepositoryUtil.applicationContext = applicationContext;
        JpaRepositoryUtil.repositories = new Repositories(applicationContext);
        JpaRepositoryUtil.repositoryInvokerFactory = new DefaultRepositoryInvokerFactory(repositories);
        log.info("{} initialized", getClass().getSimpleName());
    }
}
