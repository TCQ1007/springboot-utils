package io.github.tcq1007.springbootutils.querydsl;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public final class QueryDslPredicateBuilder<E> {

    private final Class<E> domainClass;
    private final Path<E> rootPath;

    public QueryDslPredicateBuilder(Class<E> domainClass) {
        this.domainClass = domainClass;
        this.rootPath = BooleanExpressionUtil.rootPath(domainClass);
    }

    public Class<E> domainClass() {
        return domainClass;
    }

    /**
     * 本实体各字段可用的 bo Ops：fieldName -> Ops 列表。
     */
    public Map<String, List<Ops>> fieldOps() {
        return DomainQueryCapability.opsOf(domainClass);
    }

    public boolean supportsBo(String paramName, Ops ops) {
        return DomainQueryCapability.supports(domainClass, paramName, ops);
    }

    public <R> Predicate bp(Class<R> paramClass, String paramName, R value,
                            BiFunction<Path<R>, Expression<? extends R>, Predicate> comparator) {
        Path<R> path = ExpressionUtils.path(paramClass, rootPath, paramName);
        return comparator.apply(path, ConstantImpl.create(value));
    }

    public <T> Predicate bo(String paramName, Ops ops, Class<T> paramClass, Constant<T> constant) {
        if (!supportsBo(paramName, ops)) {
            throw new IllegalArgumentException(
                    "字段 " + domainClass.getSimpleName() + "." + paramName
                            + " 不支持 Ops." + ops.name()
                            + "，可用: " + fieldOps().getOrDefault(paramName, List.of()));
        }
        return Expressions.booleanOperation(ops, ExpressionUtils.path(paramClass, rootPath, paramName), constant);
    }

    public <T> Predicate bo(String paramName, Ops ops, Class<T> paramClass, T value) {
        return bo(paramName, ops, paramClass, ConstantImpl.create(value));
    }

    public Predicate like(String paramName, String value) {
        return bo(paramName, Ops.LIKE, String.class, ConstantImpl.create("%" + value + "%"));
    }
}
