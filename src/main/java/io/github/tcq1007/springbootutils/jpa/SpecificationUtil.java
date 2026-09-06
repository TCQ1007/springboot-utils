package io.github.tcq1007.springbootutils.jpa;

import io.github.tcq1007.springbootutils.jpa.spec.PredicateOperators;
import io.github.tcq1007.springbootutils.jpa.spec.PredicateSpecifications;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.util.Collection;

/**
 * JPA 条件组装入口。只提供字段操作 API，算子与 {@code q} 工厂见 {@code jpa.spec}。
 */
public final class SpecificationUtil {

    private SpecificationUtil() {
    }

    public static <T> PredicateSpecification<T> not(PredicateSpecification<T> spec) {
        return PredicateSpecification.not(spec);
    }

    public static <T, R> PredicateSpecification<T> eq(String fieldName, R value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.eq());
    }

    public static <T, R> PredicateSpecification<T> ne(String fieldName, R value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.ne());
    }

    public static <T> PredicateSpecification<T> isNull(String fieldName) {
        return PredicateSpecifications.q(fieldName, PredicateOperators.isNull());
    }

    public static <T> PredicateSpecification<T> isNotNull(String fieldName) {
        return PredicateSpecifications.q(fieldName, PredicateOperators.isNotNull());
    }

    public static <T> PredicateSpecification<T> isTrue(String fieldName) {
        return PredicateSpecifications.q(fieldName, PredicateOperators.isTrue());
    }

    public static <T> PredicateSpecification<T> isFalse(String fieldName) {
        return PredicateSpecifications.q(fieldName, PredicateOperators.isFalse());
    }

    public static <T> PredicateSpecification<T> like(String fieldName, String value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.like());
    }

    public static <T> PredicateSpecification<T> notLike(String fieldName, String value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.notLike());
    }

    public static <T> PredicateSpecification<T> contains(String fieldName, String value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.contains());
    }

    public static <T> PredicateSpecification<T> startsWith(String fieldName, String value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.startsWith());
    }

    public static <T> PredicateSpecification<T> endsWith(String fieldName, String value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.endsWith());
    }

    public static <T> PredicateSpecification<T> likeIgnoreCase(String fieldName, String value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.likeIgnoreCase());
    }

    public static <T> PredicateSpecification<T> containsIgnoreCase(String fieldName, String value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.containsIgnoreCase());
    }

    public static <T, R> PredicateSpecification<T> in(String fieldName, Collection<R> values) {
        return PredicateSpecifications.q(fieldName, values, PredicateOperators.in());
    }

    public static <T, R> PredicateSpecification<T> notIn(String fieldName, Collection<R> values) {
        return PredicateSpecifications.q(fieldName, values, PredicateOperators.notIn());
    }

    public static <T, R extends Comparable<? super R>> PredicateSpecification<T> gt(String fieldName, R value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.gt());
    }

    public static <T, R extends Comparable<? super R>> PredicateSpecification<T> ge(String fieldName, R value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.ge());
    }

    public static <T, R extends Comparable<? super R>> PredicateSpecification<T> lt(String fieldName, R value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.lt());
    }

    public static <T, R extends Comparable<? super R>> PredicateSpecification<T> le(String fieldName, R value) {
        return PredicateSpecifications.q(fieldName, value, PredicateOperators.le());
    }

    public static <T, R extends Comparable<? super R>> PredicateSpecification<T> between(String fieldName, R left, R right) {
        return PredicateSpecifications.q(fieldName, Pair.of(left, right), PredicateOperators.between());
    }
}
