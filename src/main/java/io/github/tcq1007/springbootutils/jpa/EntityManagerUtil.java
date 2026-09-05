package io.github.tcq1007.springbootutils.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnClass(EntityManager.class)
public class EntityManagerUtil {

    private static EntityManager entityManager;

    public static EntityManager get() {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManagerUtil is not initialized");
        }
        return entityManager;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        EntityManagerUtil.entityManager = entityManager;
        log.info("{} initialized", getClass().getSimpleName());
    }
}
