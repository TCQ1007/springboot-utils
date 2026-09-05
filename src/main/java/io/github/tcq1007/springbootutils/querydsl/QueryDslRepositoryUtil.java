package io.github.tcq1007.springbootutils.querydsl;

import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnClass({ QuerydslJpaPredicateExecutor.class, EntityManager.class, SimpleEntityPathResolver.class })
public class QueryDslRepositoryUtil implements ApplicationContextAware {

    private static final Map<Class<?>, QuerydslJpaPredicateExecutor<?>> querydslJpaPredicateExecutorMap = new ConcurrentHashMap<>();
    private static ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    public static <T> QuerydslJpaPredicateExecutor<T> getQuerydslJpaPredicateExecutor(Class<T> domainClass) {
        return (QuerydslJpaPredicateExecutor<T>)
                querydslJpaPredicateExecutorMap.computeIfAbsent(domainClass,
                        QueryDslRepositoryUtil::genQueryDslJpaPredicateExecutor);
    }

    public static <T> QuerydslJpaPredicateExecutor<T> genQueryDslJpaPredicateExecutor(Class<T> domainClass) {
        EntityManager entityManager = entityManager();
        return new QuerydslJpaPredicateExecutor<>(
                JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager),
                entityManager,
                SimpleEntityPathResolver.INSTANCE,
                null);
    }

    private static EntityManager entityManager() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                    "QueryDslRepositoryUtil not properly initialized. "
                            + "Please ensure this bean is managed by Spring and ApplicationContext is set.");
        }
        return applicationContext.getBean(EntityManager.class);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        QueryDslRepositoryUtil.applicationContext = applicationContext;
    }
}
