package io.github.tcq1007.springbootutils.jpa.spec;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 把字段名、取值和算子组装成 {@link PredicateSpecification}。空值视为不参与过滤。
 */
public final class PredicateSpecifications {

    private PredicateSpecifications() {
    }

    public static <R, T> PredicateSpecification<T> q(String fieldName, Function<Path<R>, Predicate> pf) {
        return (root, _) -> pf.apply(root.get(fieldName));
    }

    public static <R, T> PredicateSpecification<T> q(String fieldName, R value, BiFunction<Path<R>, R, Predicate> pf) {
        return (root, builder) -> Optional.ofNullable(value)
                .map(v -> pf.apply(root.get(fieldName), v))
                .orElseGet(builder::conjunction);
    }

    public static <R, T> PredicateSpecification<T> q(String fieldName, R value,
                                                     TriFunction<Path<R>, R, CriteriaBuilder, Predicate> pf) {
        return (root, builder) -> {
            if (value == null) {
                return builder.conjunction();
            }
            if (value instanceof String s && s.isBlank()) {
                return builder.conjunction();
            }
            return pf.apply(root.get(fieldName), value, builder);
        };
    }

    public static <T, R> PredicateSpecification<T> q(String fieldName, Collection<R> values,
                                                     BiFunction<Path<R>, Collection<R>, Predicate> pf) {
        return (root, builder) -> {
            if (CollectionUtils.isEmpty(values)) {
                return builder.conjunction();
            }
            return pf.apply(root.get(fieldName), values);
        };
    }

    public static <R, T> PredicateSpecification<T> q(
            String fieldName,
            Pair<R, R> value,
            TriFunction<Path<R>, Pair<R, R>, CriteriaBuilder, Predicate> predicateFunction) {
        return (root, builder) -> {
            if (value == null || value.getLeft() == null || value.getRight() == null) {
                return builder.conjunction();
            }
            return predicateFunction.apply(root.get(fieldName), value, builder);
        };
    }
}
