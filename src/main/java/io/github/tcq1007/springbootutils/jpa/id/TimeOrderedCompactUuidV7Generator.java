package io.github.tcq1007.springbootutils.jpa.id;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;
import org.hibernate.id.uuid.UuidVersion7Strategy;

import java.util.EnumSet;

/**
 * Hibernate 主键生成器：产出紧凑十六进制 UUID v7（32 个字符，无连字符）。
 *
 * <h2>标准与委托</h2>
 * 值由 {@link UuidVersion7Strategy} 按 <a href="https://www.rfc-editor.org/rfc/rfc9562.html">RFC 9562</a>
 * 生成 128-bit UUID，再经 {@link CompactUuidUtil#toCompact} 去掉连字符。
 * 不改变位布局，只改变持久化字符串形态。
 *
 * <h2>持久化形态</h2>
 * <ul>
 *   <li>类型：{@link String}，小写十六进制 {@code [0-9a-f]}。</li>
 *   <li>长度：固定 {@value CompactUuidUtil#UUID_HEX_LENGTH}（标准带连字符形式为 36）。</li>
 *   <li>示例：{@code 0192e6b4c3a17b8e9f0a1b2c3d4e5f60}
 *       对应 canonical {@code 0192e6b4-c3a1-7b8e-9f0a-1b2c3d4e5f60}。</li>
 *   <li>不能直接交给 {@link java.util.UUID#fromString(String)}；需按 8-4-4-4-12 插回连字符。</li>
 * </ul>
 *
 * <h2>128-bit 布局（高位 → 低位）</h2>
 * <pre>
 *  48 bit  Unix 纪元毫秒（大端，不含闰秒）
 *   4 bit  version = 0b0111（7）
 *  12 bit  亚毫秒时间（约 1/4096 ms，用于同毫秒内再排序）
 *   2 bit  variant = 0b10（RFC 4122）
 *  62 bit  SecureRandom 伪随机计数，兼顾单调、唯一与熵
 * </pre>
 * 48-bit 毫秒时间戳约在公元 10889 年溢出。前 12 个十六进制字符即毫秒时间戳，可粗粒度还原生成时刻。
 *
 * <h2>按字符串做时间排序</h2>
 * 定长、仅小写十六进制、Unix 毫秒时间戳以大端编码在最前面，因此字符串字典序就是时间序：
 * {@code String#compareTo}、SQL {@code ORDER BY id}、{@code id > :cursor} 游标分页均可按创建时间前进。
 * 去掉连字符不改变此前缀序，反而避免分隔符干扰比较。同毫秒内再靠亚毫秒 12-bit 与 62-bit 计数区分；
 * 单 JVM 内该后缀也单调，跨进程则只保证毫秒前缀有序。新插入多落在 B+Tree 尾部，索引局部性优于随机 UUIDv4。
 *
 * <h2>单调与唯一</h2>
 * {@link UuidVersion7Strategy} 在 JVM 内用原子状态推进：时钟前进则换新时间戳并重采样随机计数；
 * 时钟未前进或新计数未大于上次时，将时间戳向前拨约 245 ns，使亚毫秒 12-bit 递增，避免回退。
 * 单 JVM 内生成序列单调不减。多实例之间不协调，同一毫秒仍各有 62-bit 熵，碰撞概率可忽略。
 * 随机源为 {@link java.security.SecureRandom}。
 *
 * <h2>生成时机</h2>
 * {@link BeforeExecutionGenerator} + {@link EventTypeSets#INSERT_ONLY}：在 INSERT 执行前于应用侧生成，
 * 不访问数据库 sequence。字段已有非空值时原样返回，便于预先分配 ID。
 *
 * @see TimeOrderedCompactUuidV7
 * @see CompactUuidUtil
 * @see UuidVersion7Strategy
 */
public class TimeOrderedCompactUuidV7Generator implements BeforeExecutionGenerator {

    private static final UuidVersion7Strategy DELEGATE = UuidVersion7Strategy.INSTANCE;

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        if (currentValue != null) {
            return currentValue;
        }
        return CompactUuidUtil.toCompact(DELEGATE.generateUuid(session));
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EventTypeSets.INSERT_ONLY;
    }
}
