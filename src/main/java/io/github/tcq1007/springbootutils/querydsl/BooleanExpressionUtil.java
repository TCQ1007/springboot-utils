package io.github.tcq1007.springbootutils.querydsl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;

import java.beans.Introspector;
import java.util.function.BiFunction;

public final class BooleanExpressionUtil {

    private BooleanExpressionUtil() {
    }

    public static <T> BooleanExpression custom(Class<T> domainClass, String fieldName, Object value,
                                               BiFunction<Path<?>, Object, BooleanExpression> operator) {
        PathBuilderFactory pathBuilderFactory = new PathBuilderFactory();
        PathBuilder<T> pathBuilder = pathBuilderFactory.create(domainClass);
        Path<?> path = pathBuilder.get(fieldName);
        return operator.apply(path, value);
    }

    public static <T> Path<T> rootPath(Class<T> domainClass) {
        return ExpressionUtils.path(domainClass, entityVariable(domainClass));
    }

    public static String entityVariable(Class<?> domainClass) {
        return Introspector.decapitalize(domainClass.getSimpleName());
    }
}
