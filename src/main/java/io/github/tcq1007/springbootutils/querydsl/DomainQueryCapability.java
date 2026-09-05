package io.github.tcq1007.springbootutils.querydsl;

import com.querydsl.core.types.Ops;
import io.github.tcq1007.springbootutils.jpa.EntityManagerUtil;
import jakarta.persistence.metamodel.Attribute;
import org.apache.commons.lang3.ClassUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按实体字段类型，给出当前 {@link QueryDslPredicateBuilder#bo}（path + 单 Constant）可用的 {@link Ops}。
 */
public final class DomainQueryCapability {

    private static final List<Ops> STRING_OPS = List.of(
            Ops.EQ, Ops.NE, Ops.EQ_IGNORE_CASE,
            Ops.LIKE, Ops.LIKE_IC,
            Ops.STARTS_WITH, Ops.STARTS_WITH_IC,
            Ops.ENDS_WITH, Ops.ENDS_WITH_IC,
            Ops.STRING_CONTAINS, Ops.STRING_CONTAINS_IC,
            Ops.MATCHES, Ops.MATCHES_IC
    );

    private static final List<Ops> COMPARE_OPS = List.of(
            Ops.EQ, Ops.NE, Ops.LT, Ops.GT, Ops.LOE, Ops.GOE
    );

    private static final List<Ops> EQ_OPS = List.of(Ops.EQ, Ops.NE);

    private static final Map<Class<?>, Map<String, List<Ops>>> OPS_CACHE = new ConcurrentHashMap<>();

    private DomainQueryCapability() {
    }

    public static Map<String, Object> attrs(Class<?> domainClass) throws IntrospectionException {
        Map<String, Object> javaTypes = new LinkedHashMap<>();
        for (Attribute<?, ?> attr : attributesOf(domainClass)) {
            javaTypes.put(attr.getName(), attr.getJavaType().getSimpleName());
        }
        List<String> beanInfo = new ArrayList<>();
        for (PropertyDescriptor descriptor : Introspector.getBeanInfo(domainClass).getPropertyDescriptors()) {
            if (descriptor.getReadMethod() == null) {
                continue;
            }
            beanInfo.add(descriptor.getName() + "|" + descriptor.getReadMethod().getName());
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("javatype", javaTypes);
        result.put("opsof", opsOf(domainClass));
        result.put("beanInfo", beanInfo);
        return result;
    }

    /**
     * fieldName -> 可用 Ops（仅 BASIC 标量字段）。
     */
    public static Map<String, List<Ops>> opsOf(Class<?> domainClass) {
        return OPS_CACHE.computeIfAbsent(domainClass, DomainQueryCapability::computeOps);
    }

    public static List<Ops> opsOfType(Class<?> type) {
        if (type == String.class) {
            return STRING_OPS;
        }
        if (type == boolean.class || type == Boolean.class || type.isEnum()) {
            return EQ_OPS;
        }
        Class<?> wrapped = ClassUtils.primitiveToWrapper(type);
        if (Number.class.isAssignableFrom(wrapped)
                || type.isPrimitive() && type != boolean.class && type != char.class) {
            return COMPARE_OPS;
        }
        if (Date.class.isAssignableFrom(type) || Temporal.class.isAssignableFrom(type)) {
            return COMPARE_OPS;
        }
        return EQ_OPS;
    }

    public static boolean supports(Class<?> domainClass, String fieldName, Ops ops) {
        List<Ops> opsList = opsOf(domainClass).get(fieldName);
        return opsList != null && opsList.contains(ops);
    }

    private static Map<String, List<Ops>> computeOps(Class<?> domainClass) {
        Map<String, List<Ops>> map = new LinkedHashMap<>();
        for (Attribute<?, ?> attr : attributesOf(domainClass)) {
            if (attr.isAssociation() || attr.isCollection()) {
                continue;
            }
            if (attr.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
                continue;
            }
            map.put(attr.getName(), opsOfType(attr.getJavaType()));
        }
        return Map.copyOf(map);
    }

    @SuppressWarnings("unchecked")
    private static Set<Attribute<?, ?>> attributesOf(Class<?> domainClass) {
        return (Set<Attribute<?, ?>>) (Set<?>) EntityManagerUtil.get()
                .getMetamodel()
                .entity(domainClass)
                .getAttributes();
    }
}
