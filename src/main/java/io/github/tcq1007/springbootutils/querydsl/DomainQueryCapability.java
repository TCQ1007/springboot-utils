package io.github.tcq1007.springbootutils.querydsl;

import io.github.tcq1007.springbootutils.jpa.EntityManagerUtil;

import com.querydsl.core.types.Ops;
import jakarta.persistence.metamodel.Attribute;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.time.temporal.Temporal;
import java.util.*;
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

    private DomainQueryCapability() {
    }

    public static Map<String, Object> attrs(Class<?> domainClass) throws IntrospectionException {
        Map<String, Object> map = new ConcurrentHashMap<>();
        Map<String, Object> attrsmap = new ConcurrentHashMap<>();
        for (Attribute<?, ?> attr : attributesOf(domainClass)) {
            attrsmap.put(attr.getName(), attr.getJavaType().getSimpleName());
        }
        map.put("javatype", attrsmap);
        map.put("opsof", opsOf(domainClass));
        List<String> nameList = new ArrayList<>();
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(domainClass).getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : Arrays.stream(propertyDescriptors).toList()) {
            String name = propertyDescriptor.getName();
            nameList.add(name + "|" + propertyDescriptor.getReadMethod().getName());
        }
        map.put("beanInfo", nameList);
        return map;
    }

    /**
     * fieldName -> 鍙敤 Ops锛堜粎 BASIC 鏍囬噺瀛楁锛?     */
    public static Map<String, List<Ops>> opsOf(Class<?> domainClass) {
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

    public static List<Ops> opsOfType(Class<?> type) {
        if (type == String.class) {
            return STRING_OPS;
        }
        if (type == boolean.class || type == Boolean.class || type.isEnum()) {
            return EQ_OPS;
        }
        if (Number.class.isAssignableFrom(wrap(type))
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

    @SuppressWarnings("unchecked")
    private static Set<Attribute<?, ?>> attributesOf(Class<?> domainClass) {
        return (Set<Attribute<?, ?>>) (Set<?>) EntityManagerUtil.get()
                .getMetamodel()
                .entity(domainClass)
                .getAttributes();
    }

    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        return switch (type.getName()) {
            case "byte" -> Byte.class;
            case "short" -> Short.class;
            case "int" -> Integer.class;
            case "long" -> Long.class;
            case "float" -> Float.class;
            case "double" -> Double.class;
            case "char" -> Character.class;
            case "boolean" -> Boolean.class;
            default -> type;
        };
    }
}
