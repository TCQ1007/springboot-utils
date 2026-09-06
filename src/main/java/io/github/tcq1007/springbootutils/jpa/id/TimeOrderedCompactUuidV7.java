package io.github.tcq1007.springbootutils.jpa.id;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * 将字段标记为由 {@link TimeOrderedCompactUuidV7Generator} 生成的主键。
 * <p>
 * 产出 RFC 9562 UUID v7 的紧凑十六进制：32 个小写字符、无连字符；
 * 定长十六进制且时间戳在最高位，可直接按字符串字典序做时间排序（{@code ORDER BY id}）。
 * 建议配合 {@code @Column(length = CompactUuidUtil.UUID_HEX_LENGTH, updatable = false)}。
 * 位布局、单调性与碰撞特性见生成器 JavaDoc。
 */
@IdGeneratorType(TimeOrderedCompactUuidV7Generator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD})
public @interface TimeOrderedCompactUuidV7 {
}
