package io.github.tcq1007.springbootutils.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 閫氳繃 {@link PersistenceContext} 娉ㄥ叆骞堕潤鎬佹毚闇?{@link EntityManager}銆? */
@Component
@ConditionalOnClass(EntityManager.class)
public class EntityManagerUtil {

    private static EntityManager entityManagerProvider;

    public static EntityManager get() {
        if (entityManagerProvider == null) {
            throw new IllegalStateException("EntityManagerUtil not initialized");
        }

        return entityManagerProvider;
    }

    @PersistenceContext
    public void setEntityManagerProvider(EntityManager provider) {
        EntityManagerUtil.entityManagerProvider = provider;
    }
}
