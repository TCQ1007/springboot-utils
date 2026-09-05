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

    public static final Map<String, Class<?>> entityName2ClassMap = new ConcurrentHashMap<>();
    private static EntityManager entityManager;
    private static EntityScanner entityScanner;

    public static Set<EntityType<?>> getEntityTypeList() {
        return entityManager.getMetamodel().getEntities();
    }

    @SuppressWarnings("unchecked")
    public static Set<Attribute<?, ?>> getAttributeList(Class<?> domainClass) {
        return (Set<Attribute<?, ?>>) getEntityType(domainClass).getAttributes();
    }

    public static Set<Class<?>> getAllDomainClass() {
        try {
            return entityScanner.scan(Entity.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> EntityType<T> getEntityType(Class<T> domainClass) {
        Metamodel metamodel = entityManager.getMetamodel();
        return metamodel.entity(domainClass);
    }

    public static <T> JpaEntityInformation<T, ?> getEntityInformation(Class<T> domainClass) {
        return JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
    }

    public static Class<?> getDomainClass(String entityName) {
        return entityName2ClassMap.get(entityName);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        log.info("EntityUtil setApplicationContext");
        EntityUtil.entityManager = applicationContext.getBean(EntityManager.class);
        EntityUtil.entityScanner = new EntityScanner(applicationContext);
        for (Class<?> domainClass : getAllDomainClass()) {
            entityName2ClassMap.put(domainClass.getSimpleName(), domainClass);
        }
    }
}
