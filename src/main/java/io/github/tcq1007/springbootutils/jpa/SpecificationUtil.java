package io.github.tcq1007.springbootutils.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SpecificationUtil {

//    public static <T> PredicateSpecification<T> build(MultiValuedMap<String, Object> paramsMap) {
//        Spec<T> s = Spec.of();
//        List<PredicateSpecification<T>> list = new ArrayList<>();
//        for (String key : paramsMap.keySet()) {
//            Collection<Object> values = paramsMap.get(key);
//            if (values == null || values.isEmpty()) {
//                continue;
//            }
//            if (values.size() == 1) {
//                list.add(s.eq(key, values.iterator().next()));
//            } else {
//                list.add(s.in(key, values));
//            }
//        }
//        return PredicateSpecification.allOf(list);
//    }

    /**
     * 绠楀瓙甯搁噺锛氫簩鍏冿紙path + value锛変笌涓夊厓锛堥渶瑕?CriteriaBuilder锛夊垎寮€銆?     */
    public interface FConstant {

        static <R> BiFunction<Path<R>, R, Predicate> eq() {
            return Expression::equalTo;
        }

        static <R> BiFunction<Path<R>, R, Predicate> ne() {
            return Expression::notEqualTo;
        }

        static <R> Function<Path<R>, Predicate> isNull() {
            return Expression::isNull;
        }

        static <R> Function<Path<R>, Predicate> isNotNull() {
            return Expression::isNotNull;
        }

        static Function<Path<Boolean>, Predicate> isTrue() {
            return path -> path.equalTo(Boolean.TRUE);
        }

        static Function<Path<Boolean>, Predicate> isFalse() {
            return path -> path.equalTo(Boolean.FALSE);
        }

        static <R> BiFunction<Path<R>, Collection<R>, Predicate> in() {
            return Path::in;
        }

        static <R> BiFunction<Path<R>, Collection<R>, Predicate> notIn() {
            return (path, values) -> path.in(values).not();
        }

        static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> like() {
            return (path, pattern, cb) -> cb.like(path, pattern);
        }

        static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> notLike() {
            return (path, pattern, cb) -> cb.notLike(path, pattern);
        }

        static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> contains() {
            return (path, value, cb) -> cb.like(path, "%" + value + "%");
        }

        static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> startsWith() {
            return (path, value, cb) -> cb.like(path, value + "%");
        }

        static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> endsWith() {
            return (path, value, cb) -> cb.like(path, "%" + value);
        }

        static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> likeIgnoreCase() {
            return (path, pattern, cb) -> cb.like(cb.lower(path), pattern.toLowerCase());
        }

        static TriFunction<Path<String>, String, CriteriaBuilder, Predicate> containsIgnoreCase() {
            return (path, value, cb) -> cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
        }

        static <R extends Comparable<? super R>> TriFunction<Path<R>, R, CriteriaBuilder, Predicate> gt() {
            return (path, value, cb) -> cb.greaterThan(path, value);
        }

        static <R extends Comparable<? super R>> TriFunction<Path<R>, R, CriteriaBuilder, Predicate> ge() {
            return (path, value, cb) -> cb.greaterThanOrEqualTo(path, value);
        }

        static <R extends Comparable<? super R>> TriFunction<Path<R>, R, CriteriaBuilder, Predicate> lt() {
            return (path, value, cb) -> cb.lessThan(path, value);
        }

        static <R extends Comparable<? super R>> TriFunction<Path<R>, R, CriteriaBuilder, Predicate> le() {
            return (path, value, cb) -> cb.lessThanOrEqualTo(path, value);
        }

        static <R extends Comparable<? super R>> TriFunction<Path<R>, Pair<R, R>, CriteriaBuilder, Predicate> between() {
            return (path, pair, cb) -> cb.between(path, pair.getLeft(), pair.getRight());
        }
    }

    /**
     * 甯﹀疄浣撶被鍨嬪弬鏁扮殑鏉′欢宸ュ巶銆傚厛 {@code Spec<UserEntity> s = Spec.of()}锛屽啀閾惧紡鎷兼潯浠讹紝
     * {@code T} 涓嶄細鎺夋垚 {@code Object}銆?     */
    public static final class Spec<T> {

        private Spec() {
        }

        public static <T> Spec<T> of() {
            return new Spec<>();
        }

        public static <T> Spec<T> of(Class<T> entityType) {
            return new Spec<>();
        }

        public PredicateSpecification<T> not(PredicateSpecification<T> spec) {
            return PredicateSpecification.not(spec);
        }

        public <R> PredicateSpecification<T> eq(String fieldName, R value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.eq());
        }

        public PredicateSpecification<T> ne(String fieldName, Object value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.ne());
        }

        public PredicateSpecification<T> isNull(String fieldName) {
            return PredicateSpecificationS.q(fieldName, FConstant.isNull());
        }

        public PredicateSpecification<T> isNotNull(String fieldName) {
            return PredicateSpecificationS.q(fieldName, FConstant.isNotNull());
        }

        public PredicateSpecification<T> isTrue(String fieldName) {
            return PredicateSpecificationS.q(fieldName, FConstant.isTrue());
        }

        public PredicateSpecification<T> isFalse(String fieldName) {
            return PredicateSpecificationS.q(fieldName, FConstant.isFalse());
        }

        public PredicateSpecification<T> like(String fieldName, String value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.like());
        }

        public PredicateSpecification<T> notLike(String fieldName, String value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.notLike());
        }

        public PredicateSpecification<T> contains(String fieldName, String value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.contains());
        }

        public PredicateSpecification<T> startsWith(String fieldName, String value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.startsWith());
        }

        public PredicateSpecification<T> endsWith(String fieldName, String value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.endsWith());
        }

        public PredicateSpecification<T> likeIgnoreCase(String fieldName, String value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.likeIgnoreCase());
        }

        public PredicateSpecification<T> containsIgnoreCase(String fieldName, String value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.containsIgnoreCase());
        }

        public <R> PredicateSpecification<T> in(String fieldName, Collection<R> values) {
            return PredicateSpecificationS.q(fieldName, values, FConstant.in());
        }

        public <R> PredicateSpecification<T> notIn(String fieldName, Collection<R> values) {
            return PredicateSpecificationS.q(fieldName, values, FConstant.notIn());
        }

        public <R extends Comparable<? super R>> PredicateSpecification<T> gt(String fieldName, R value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.gt());
        }

        public <R extends Comparable<? super R>> PredicateSpecification<T> ge(String fieldName, R value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.ge());
        }

        public <R extends Comparable<? super R>> PredicateSpecification<T> lt(String fieldName, R value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.lt());
        }

        public <R extends Comparable<? super R>> PredicateSpecification<T> le(String fieldName, R value) {
            return PredicateSpecificationS.q(fieldName, value, FConstant.le());
        }

        public <R extends Comparable<? super R>> PredicateSpecification<T> between(String fieldName, R left, R right) {
            return PredicateSpecificationS.q(fieldName, Pair.of(left, right), FConstant.between());
        }
    }

    /**
     * 寤鸿鐢?{@link PredicateSpecificationS}锛泏@link Spec} 榛樿璧板畠銆?     */
    @Deprecated
    public static class SpecificationS {

        public static <R, T> Specification<T> q(String fieldName, Function<Path<R>, Predicate> pf) {
            return (root, _, builder) -> builder.and(pf.apply(root.get(fieldName)));
        }

        public static <R, T> Specification<T> q(String fieldName, R value, BiFunction<Path<R>, R, Predicate> pf) {
            return (root, _, builder) ->
                    Optional.ofNullable(value).map(v -> builder.and(pf.apply(root.get(fieldName), v))).orElseGet(builder::conjunction);
        }

        public static <R, T> Specification<T> q(String fieldName, R value, TriFunction<Path<R>, R, CriteriaBuilder, Predicate> pf) {
            return (root, _, builder) ->
                    Optional.ofNullable(value).map(v -> builder.and(pf.apply(root.get(fieldName), v, builder))).orElseGet(builder::conjunction);
        }

        public static <T> Specification<T> q(String fieldName, Collection<?> values, BiFunction<Path<?>, Collection<?>, Predicate> pf) {
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

    public static class PredicateSpecificationS {

        public static <R, T> PredicateSpecification<T> q(String fieldName, Function<Path<R>, Predicate> pf) {
            return (root, _) -> pf.apply(root.get(fieldName));
        }

        public static <R, T> PredicateSpecification<T> q(String fieldName, R value, BiFunction<Path<R>, R, Predicate> pf) {
            return (root, builder) -> Optional.ofNullable(value)
                    .map(v -> pf.apply(root.get(fieldName), v))
                    .orElseGet(builder::conjunction);
        }

        public static <R, T> PredicateSpecification<T> q(String fieldName, R value, TriFunction<Path<R>, R, CriteriaBuilder, Predicate> pf) {
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
}
