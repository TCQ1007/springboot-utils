package io.github.tcq1007.springbootutils.ddd.interfaces;

import java.io.Serializable;

/**
 * 值对象标记接口：无独立生命周期，通过属性值判断相等性。
 */
public interface ValueObject extends Serializable {
}
