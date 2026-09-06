package io.github.tcq1007.springbootutils.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.type.NumericBooleanConverter;

import java.time.LocalDateTime;

/**
 * 审计字段基类：数据库维护创建/更新时间，逻辑删除由 {@link SoftDelete} 管理。
 */
@SoftDelete(columnName = "is_delete", converter = NumericBooleanConverter.class)
@MappedSuperclass
@Getter
@Setter
public abstract class BaseAuditEntity {

    @Column(name = "create_time", nullable = false, updatable = false, insertable = false,
            columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, insertable = false, updatable = false,
            columnDefinition = "DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)")
    private LocalDateTime updateTime;

    @Column(name = "is_delete", insertable = false, updatable = false,
            columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private boolean deleted;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "BIGINT NOT NULL DEFAULT 0")
    private Long version;
}
