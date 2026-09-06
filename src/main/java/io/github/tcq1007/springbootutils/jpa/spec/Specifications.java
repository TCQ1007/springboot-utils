package io.github.tcq1007.springbootutils.jpa.spec;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 组装旧的 {@link Specification}。新代码请用 {@link PredicateSpecifications}。
 */
public final class Specifications {

    private Specifications() {
    }

    public static <R, T> Specification<T> q(String fieldName, Function<Path<R>, Predicate> pf) {
        return (root, _, builder) -> builder.and(pf.apply(root.get(fieldName)));
    }

    public static <R, T> Specification<T> q(String fieldName, R value, BiFunction<Path<R>, R, Predicate> pf) {
        return (root, _, builder) ->
                Optional.ofNullable(value)
                        .map(v -> builder.and(pf.apply(root.get(fieldName), v)))
                        .orElseGet(builder::conjunction);
    }

    public static <R, T> Specification<T> q(String fieldName, R value,
                                            TriFunction<Path<R>, R, CriteriaBuilder, Predicate> pf) {
        return (root, _, builder) ->
                Optional.ofNullable(value)
                        .map(v -> builder.and(pf.apply(root.get(fieldName), v, builder)))
                        .orElseGet(builder::conjunction);
    }

    public static <T> Specification<T> q(String fieldName, Collection<?> values,
                                         BiFunction<Path<?>, Collection<?>, Predicate> pf) {
        return (root, _, builder) -> {
            if (values == null || values.isEmpty()) {
                return builder.conjunction();
            }
            return builder.and(pf.apply(root.get(fieldName), values));
        };
    }

    public static <R, T> Specification<T> q(
            String fieldName,
            Pair<R, R> value,
            TriFunction<Path<R>, Pair<R, R>, CriteriaBuilder, Predicate> pf) {
        return (root, _, builder) -> {
            if (value == null || value.getLeft() == null || value.getRight() == null) {
                return builder.conjunction();
            }
            return builder.and(pf.apply(root.get(fieldName), value, builder));
        };
    }
}
