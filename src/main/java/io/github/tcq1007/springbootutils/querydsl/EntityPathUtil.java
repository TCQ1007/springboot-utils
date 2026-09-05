package io.github.tcq1007.springbootutils.querydsl;

import com.querydsl.core.types.EntityPath;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

public final class EntityPathUtil {

    private EntityPathUtil() {
    }

    public static <T> EntityPath<T> createPath(Class<T> domainClass) {
        return SimpleEntityPathResolver.INSTANCE.createPath(domainClass);
    }
}
