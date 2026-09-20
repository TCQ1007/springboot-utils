package io.github.tcq1007.springbootutils.ddd.annotation;

/**
 * 标识符值对象：聚合根 / 实体的 typed id。
 */
public interface Identifier<T> extends ValueObject {

    T value();
}
