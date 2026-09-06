package io.github.tcq1007.springbootutils.jpa.entity;

import io.github.tcq1007.springbootutils.jpa.id.CompactUuidUtil;
import io.github.tcq1007.springbootutils.jpa.id.TimeOrderedCompactUuidV7;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 持久化实体基类：紧凑十六进制 UUID v7 主键 + 审计字段。
 */
@MappedSuperclass
@ToString
@Getter
@Setter
public abstract class BaseUUIDV7IDEntity extends BaseAuditEntity {

    @Id
    @TimeOrderedCompactUuidV7
    @Column(length = CompactUuidUtil.UUID_HEX_LENGTH, nullable = false, updatable = false)
    private String id;
}
