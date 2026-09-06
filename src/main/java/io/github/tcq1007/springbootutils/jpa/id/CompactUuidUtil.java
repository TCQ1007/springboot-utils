package io.github.tcq1007.springbootutils.jpa.id;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * 将 {@link UUID} 编成定长紧凑十六进制（32 个小写字符，无连字符）。
 * <p>
 * JDK 保证 {@link UUID#toString()} 为固定的 8-4-4-4-12 小写十六进制，去掉 4 个连字符即紧凑形态；
 * {@link #fromCompact(String)} / {@link #toCanonical(String)} 可再还原。
 * UUID v7 的 Unix 毫秒时间戳在最高 48 位，可用 {@link #v7Instant(UUID)} 还原。
 */
public final class CompactUuidUtil {

    /** 标准 UUID 去掉连字符后的固定长度。 */
    public static final int UUID_HEX_LENGTH = 32;

    private CompactUuidUtil() {
    }

    public static String toCompact(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    /**
     * 紧凑 32 位十六进制 → 标准 canonical：{@code 8-4-4-4-12}。
     */
    public static String toCanonical(String compactHex) {
        requireCompact(compactHex);
        return compactHex.substring(0, 8)
                + '-' + compactHex.substring(8, 12)
                + '-' + compactHex.substring(12, 16)
                + '-' + compactHex.substring(16, 20)
                + '-' + compactHex.substring(20, 32);
    }

    /**
     * 紧凑 32 位十六进制 → {@link UUID}，与 {@link #toCompact(UUID)} 互为逆操作。
     */
    public static UUID fromCompact(String compactHex) {
        return UUID.fromString(toCanonical(compactHex));
    }

    /**
     * 取出 UUID v7 的 Unix 纪元毫秒。不要用 {@link UUID#timestamp()}，那只适用于 v1。
     * <p>
     * 高 64 位布局为 {@code [48-bit millis][4-bit version][12-bit sub-ms]}，无符号右移 16 位即毫秒。
     */
    public static long v7EpochMilli(UUID uuid) {
        requireV7(uuid.version());
        return uuid.getMostSignificantBits() >>> 16;
    }

    /**
     * 从紧凑十六进制取出 UUID v7 的 Unix 纪元毫秒：前 12 个字符即 48-bit 时间戳。
     */
    public static long v7EpochMilli(String compactHex) {
        requireCompactV7(compactHex);
        return Long.parseLong(compactHex.substring(0, 12), 16);
    }

    public static Instant v7Instant(UUID uuid) {
        return Instant.ofEpochMilli(v7EpochMilli(uuid));
    }

    public static Instant v7Instant(String compactHex) {
        return Instant.ofEpochMilli(v7EpochMilli(compactHex));
    }

    /**
     * UUID v7 存的是 UTC 毫秒，{@link LocalDateTime} 无时区，必须指定 {@code zone} 才能落到墙上时钟。
     */
    public static LocalDateTime v7LocalDateTime(UUID uuid, ZoneId zone) {
        return toLocalDateTime(v7EpochMilli(uuid), zone);
    }

    public static LocalDateTime v7LocalDateTime(String compactHex, ZoneId zone) {
        return toLocalDateTime(v7EpochMilli(compactHex), zone);
    }

    private static LocalDateTime toLocalDateTime(long epochMilli, ZoneId zone) {
        return Instant.ofEpochMilli(epochMilli).atZone(zone).toLocalDateTime();
    }

    private static void requireV7(int version) {
        if (version != 7) {
            throw new IllegalArgumentException("not a UUID v7: version=" + version);
        }
    }

    private static void requireCompact(String compactHex) {
        if (compactHex == null || compactHex.length() != UUID_HEX_LENGTH) {
            throw new IllegalArgumentException("compact UUID must be " + UUID_HEX_LENGTH + " hex chars");
        }
    }

    private static void requireCompactV7(String compactHex) {
        requireCompact(compactHex);
        if (compactHex.charAt(12) != '7') {
            throw new IllegalArgumentException("not a UUID v7 compact id");
        }
    }
}
