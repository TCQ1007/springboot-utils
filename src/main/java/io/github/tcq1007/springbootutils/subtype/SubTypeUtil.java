package io.github.tcq1007.springbootutils.subtype;

import org.atteo.classindex.ClassIndex;
import org.atteo.classindex.IndexSubclasses;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubTypeUtil {

    /**
     * 父类 -> 子类列表 的缓存。
     * 使用 ConcurrentHashMap 保证线程安全，computeIfAbsent 保证只计算一次。
     */
    private static final Map<Class<?>, List<Class<?>>> CACHE = new ConcurrentHashMap<>();

    private SubTypeUtil() {
        // 工具类，禁止实例化
    }

    /**
     * 获取某个父类/接口的所有子类，结果会被缓存。
     *
     * @param superClass 父类或接口的 Class 对象, superClass上需要使用 {@link IndexSubclasses} 注解
     *                   子类所在的项目需要使用相应的注解处理器
     * @param <T>        父类型
     * @return 不可变的子类列表，可能为空
     */
    @SuppressWarnings("unchecked")
    public static <T> List<Class<? extends T>> getSubTypesByType(Class<T> superClass) {
        // computeIfAbsent 在 key 不存在时才执行 lambda，且保证原子性
        List<Class<?>> raw = CACHE.computeIfAbsent(superClass, SubTypeUtil::load);
        // 由于 load 时按 superClass 查询，泛型是安全的
        return (List<Class<? extends T>>) (List<?>) Collections.unmodifiableList(raw);
    }

    @SuppressWarnings("unchecked")
    public static List<Class<?>> getSubTypes(Class<?> superClass) {
        return CACHE.computeIfAbsent(superClass, SubTypeUtil::load);
    }

    /**
     * 一次性预热多个父类，避免运行时首次查询的抖动。
     * 适合在应用启动阶段调用。
     */
    public static void warmUp(Class<?>... superClasses) {
        for (Class<?> superClass : superClasses) {
            getSubTypes(superClass);
        }
    }

    /**
     * 清空缓存，主要用于测试或热部署场景。
     */
    public static void clear() {
        CACHE.clear();
    }

    /**
     * 实际加载逻辑：读取 ClassIndex 的编译期索引。
     */
    private static List<Class<?>> load(Class<?> superClass) {
        List<Class<?>> classes = List.of();
        ClassIndex.getSubclasses(superClass).iterator().forEachRemaining(classes::add);
        return classes;
    }

}
