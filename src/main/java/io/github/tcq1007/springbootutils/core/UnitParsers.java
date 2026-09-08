package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.boot.convert.PeriodStyle;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.time.Period;
import java.util.function.Function;

@Slf4j
public class UnitParsers {

    private static final UnitParser<Duration> DURATION_PARSER = new DurationParser();
    private static final UnitParser<DataSize> DATA_SIZE_PARSER = new DataSizeParser();
    private static final UnitParser<Period> PERIOD_PARSER = new PeriodParser();


    public static UnitParser<Duration> duration() {
        return DURATION_PARSER;
    }

    public static UnitParser<DataSize> dataSize() {
        return DATA_SIZE_PARSER;
    }

    public static UnitParser<Period> period() {
        return PERIOD_PARSER;
    }


    public interface UnitParser<T> {
        boolean isValid(String value);
        T parse(String value);
    }

    static class DurationParser implements UnitParser<Duration> {

        /**
         * Check if the given string is a valid duration string.
         *
         * @param value the duration string to check, e.g. "1d", "1h", "1m", "1s"
         * @return true if the string is a valid duration string, false otherwise
         */
        @Override
        public boolean isValid(String value) {
            return checkValid("duration", value, DurationStyle::detect);
        }

        /**
         * Parse the given string as a {@link Duration}.
         *
         * @param value the duration string to parse, e.g. "1d", "1h", "1m", "1s"
         * @return the parsed duration
         */
        @Override
        public Duration parse(String value) {
            return doParse("duration", value, DurationStyle::detectAndParse);
        }
    }

    static class DataSizeParser implements UnitParser<DataSize> {
        @Override
        public boolean isValid(String value) {
            return checkValid("dataSize", value, DataSize::parse);
        }

        @Override
        public DataSize parse(String value) {
            return doParse("dataSize", value, DataSize::parse);
        }
    }

    static class PeriodParser implements UnitParser<Period> {
        @Override
        public boolean isValid(String value) {
            return checkValid("period", value, PeriodStyle::detect);
        }

        @Override
        public Period parse(String value) {
            return doParse("period", value, PeriodStyle::detectAndParse);
        }
    }

    private static boolean checkValid(String kind, String value, Function<String, ?> detector) {
        try {
            detector.apply(value);
            log.debug("{} valid: {}", kind, value);
            return true;
        } catch (Exception e) {
            log.debug("invalid {}: {}", kind, value);
            return false;
        }
    }

    private static <T> T doParse(String kind, String value, Function<String, T> parser) {
        try {
            T parsed = parser.apply(value);
            log.debug("parsed {} {} -> {}", kind, value, parsed);
            return parsed;
        } catch (RuntimeException e) {
            log.warn("failed to parse {}: {}", kind, value, e);
            throw e;
        }
    }
}
