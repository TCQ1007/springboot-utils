package io.github.tcq1007.springbootutils.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.persistence.autoconfigure.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ConditionalOnClass({ EntityManager.class, EntityScanner.class })
public class EntityUtil implements ApplicationContextAware {

    private static final Map<String, Class<?>> ENTITY_NAME_TO_CLASS = new ConcurrentHashMap<>();
    private static EntityManager entityManager;
    private static EntityScanner entityScanner;

    public static Set<EntityType<?>> getEntityTypeList() {
        return requireEntityManager().getMetamodel().getEntities();
    }

    @SuppressWarnings("unchecked")
    public static Set<Attribute<?, ?>> getAttributeList(Class<?> domainClass) {
        return (Set<Attribute<?, ?>>) getEntityType(domainClass).getAttributes();
    }

    public static Set<Class<?>> getAllDomainClass() {
        try {
            return requireEntityScanner().scan(Entity.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("failed to scan JPA entities", e);
        }
    }

    public static <T> EntityType<T> getEntityType(Class<T> domainClass) {
        Metamodel metamodel = requireEntityManager().getMetamodel();
        return metamodel.entity(domainClass);
    }

    public static <T> JpaEntityInformation<T, ?> getEntityInformation(Class<T> domainClass) {
        return JpaEntityInformationSupport.getEntityInformation(domainClass, requireEntityManager());
    }

    public static Class<?> getDomainClass(String entityName) {
        return ENTITY_NAME_TO_CLASS.get(entityName);
    }

    private static EntityManager requireEntityManager() {
        if (entityManager == null) {
            throw new IllegalStateException("EntityUtil is not initialized");
        }
        return entityManager;
    }

    private static EntityScanner requireEntityScanner() {
        if (entityScanner == null) {
            throw new IllegalStateException("EntityUtil is not initialized");
        }
        return entityScanner;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        EntityUtil.entityManager = applicationContext.getBean(EntityManager.class);
        EntityUtil.entityScanner = new EntityScanner(applicationContext);
        ENTITY_NAME_TO_CLASS.clear();
        Set<Class<?>> domainClasses = getAllDomainClass();
        for (Class<?> domainClass : domainClasses) {
            ENTITY_NAME_TO_CLASS.put(domainClass.getSimpleName(), domainClass);
        }
        log.info("{} initialized, entityCount={}", getClass().getSimpleName(), domainClasses.size());
        if (log.isDebugEnabled()) {
            log.debug("entities={}", domainClasses.stream().map(Class::getName).toList());
        }
    }
}
