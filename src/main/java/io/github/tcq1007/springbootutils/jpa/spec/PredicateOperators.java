package io.github.tcq1007.springbootutils.jpa.spec;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Criteria 路径算子。二元（path + value）与三元（需要 {@link CriteriaBuilder}）分开。
 */
public final class PredicateOperators {

    private PredicateOperators() {
    }

    public static <R> BiFunction<Path<R>, R, Predicate> eq() {
        return Expression::equalTo;
    }

    public static <R> BiFunction<Path<R>, R, Predicate> ne() {
        return Expression::notEqualTo;
    }

    public static <R> Function<Path<R>, Predicate> isNull() {
        return Expression::isNull;
    }

    public static <R> Function<Path<R>, Predicate> isNotNull() {
        return Expression::isNotNull;
    }

    public static Function<Path<Boolean>, Predicate> isTrue() {
        return path -> path.equalTo(Boolean.TRUE);
    }

    public static Function<Path<Boolean>, Predicate> isFalse() {
        return path -> path.equalTo(Boolean.FALSE);
    }

    public static <R> BiFunction<Path<R>, Collection<R>, Predicate> in() {
        return Path::in;
    }

    public static <R> BiFunction<Path<R>, Collection<R>, Predicate> notIn() {
        return (path, values) -> path.in(values).not();
    }

    public static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> like() {
        return (path, pattern, cb) -> cb.like(path, pattern);
    }

    public static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> notLike() {
        return (path, pattern, cb) -> cb.notLike(path, pattern);
    }

    public static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> contains() {
        return (path, value, cb) -> cb.like(path, "%" + value + "%");
    }

    public static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> startsWith() {
        return (path, value, cb) -> cb.like(path, value + "%");
    }

    public static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> endsWith() {
        return (path, value, cb) -> cb.like(path, "%" + value);
    }

    public static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> likeIgnoreCase() {
        return (path, pattern, cb) -> cb.like(cb.lower(path), pattern.toLowerCase());
    }

    public static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> containsIgnoreCase() {
        return (path, value, cb) -> cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
    }

    public static <R extends Comparable<? super R>> TriFunction<Path<R>, R, CriteriaBuilder, Predicate> gt() {
        return (path, value, cb) -> cb.greaterThan(path, value);
    }

    public static <R extends Comparable<? super R>> TriFunction<Path<R>, R, CriteriaBuilder, Predicate> ge() {
        return (path, value, cb) -> cb.greaterThanOrEqualTo(path, value);
    }

    public static <R extends Comparable<? super R>> TriFunction<Path<R>, R, CriteriaBuilder, Predicate> lt() {
        return (path, value, cb) -> cb.lessThan(path, value);
    }

    public static <R extends Comparable<? super R>> TriFunction<Path<R>, R, CriteriaBuilder, Predicate> le() {
        return (path, value, cb) -> cb.lessThanOrEqualTo(path, value);
    }

    public static <R extends Comparable<? super R>> TriFunction<Path<R>, Pair<R, R>, CriteriaBuilder, Predicate> between() {
        return (path, pair, cb) -> cb.between(path, pair.getLeft(), pair.getRight());
    }
}
