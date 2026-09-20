package io.github.tcq1007.springbootutils.ddd.annotation;

/**
 * 聚合根标记接口：对外唯一入口，维护聚合内不变量。
 *
 * @param <ID> 聚合根标识类型
 */
public interface AggregateRoot<ID> {

    ID getId();

    /**
     * 是否尚未持久化（无全局 id）。
     */
    default boolean isNew() {
        return getId() == null;
    }
}
