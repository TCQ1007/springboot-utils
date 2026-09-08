package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.PeriodStyle;
import org.springframework.format.datetime.standard.DurationFormatterUtils;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.time.Period;

/**
 * Spring Boot 风格的时长、数据大小、周期字符串解析。
 */
@Slf4j
public class UnitParsers {

    private static final UnitParser<Duration> DURATION_PARSER = new DurationParser();
    private static final UnitParser<DataSize> DATA_SIZE_PARSER = new DataSizeParser();
    private static final UnitParser<Period> PERIOD_PARSER = new PeriodParser();


    /**
     * 时长解析器，支持 {@code 1d}、{@code 1h}、{@code 1m}、{@code 1s} 等。
     */
    public static UnitParser<Duration> duration() {
        return DURATION_PARSER;
    }

    /**
     * 数据大小解析器，支持 {@code 1B}、{@code 1KB}、{@code 1MB}、{@code 1GB} 等。
     */
    public static UnitParser<DataSize> dataSize() {
        return DATA_SIZE_PARSER;
    }

    /**
     * 周期解析器，支持 {@code 1d}、{@code 1w}、{@code 1m}、{@code 1y} 等。
     */
    public static UnitParser<Period> period() {
        return PERIOD_PARSER;
    }


    /**
     * 带单位字符串的校验与解析。
     */
    public interface UnitParser<T> {
        /**
         * 判断字符串是否为合法单位值。
         *
         * @param value 待校验字符串
         * @return 合法返回 {@code true}，否则 {@code false}
         */
        boolean isValid(String value);

        /**
         * 将字符串解析为对应单位类型。
         *
         * @param value 待解析字符串
         * @return 解析结果
         */
        T parse(String value);
    }

    static class DurationParser implements UnitParser<Duration> {

        /**
         * 判断是否为合法时长字符串，例如 {@code 1d}、{@code 1h}、{@code 1m}、{@code 1s}。
         */
        @Override
        public boolean isValid(String value) {
            try {
                DurationFormatterUtils.detect(value);
                log.debug("duration valid: {}", value);
                return true;
            } catch (Exception e) {
                log.debug("invalid duration: {}", value);
                return false;
            }
        }

        /**
         * 解析为 {@link Duration}，例如 {@code 1d}、{@code 1h}、{@code 1m}、{@code 1s}。
         */
        @Override
        public Duration parse(String value) {
            try {
                Duration duration = DurationFormatterUtils.detectAndParse(value);
                log.debug("parsed duration {} -> {}", value, duration);
                return duration;
            } catch (RuntimeException e) {
                log.warn("failed to parse duration: {}", value, e);
                throw e;
            }
        }
    }

    static class DataSizeParser implements UnitParser<DataSize> {
        /**
         * 判断是否为合法数据大小字符串，例如 {@code 1B}、{@code 1KB}、{@code 1MB}、{@code 1GB}。
         */
        @Override
        public boolean isValid(String value) {
            try {
                DataSize.parse(value);
                log.debug("dataSize valid: {}", value);
                return true;
            } catch (Exception e) {
                log.debug("invalid dataSize: {}", value);
                return false;
            }
        }

        /**
         * 解析为 {@link DataSize}，例如 {@code 1B}、{@code 1KB}、{@code 1MB}、{@code 1GB}。
         */
        @Override
        public DataSize parse(String value) {
            try {
                DataSize dataSize = DataSize.parse(value);
                log.debug("parsed dataSize {} -> {}", value, dataSize);
                return dataSize;
            } catch (RuntimeException e) {
                log.warn("failed to parse dataSize: {}", value, e);
                throw e;
            }
        }
    }

    static class PeriodParser implements UnitParser<Period> {
        /**
         * 判断是否为合法周期字符串，例如 {@code 1d}、{@code 1w}、{@code 1m}、{@code 1y}。
         */
        @Override
        public boolean isValid(String value) {
            try {
                PeriodStyle.detect(value);
                log.debug("period valid: {}", value);
                return true;
            } catch (Exception e) {
                log.debug("invalid period: {}", value);
                return false;
            }
        }

        /**
         * 解析为 {@link Period}，例如 {@code 1d}、{@code 1w}、{@code 1m}、{@code 1y}。
         */
        @Override
        public Period parse(String value) {
            try {
                Period period = PeriodStyle.detectAndParse(value);
                log.debug("parsed period {} -> {}", value, period);
                return period;
            } catch (RuntimeException e) {
                log.warn("failed to parse period: {}", value, e);
                throw e;
            }
        }
    }
}
