package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import nl.talsmasoftware.lazy4j.Lazy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@ConditionalOnClass(JsonMapper.class)
public class JsonUtil {

    private static final Lazy<JsonMapper> JSON_MAPPER = SpringBeanUtil.getLazy(JsonMapper.class);

    public static String toJson(Object value) {
        String json = mapper().writeValueAsString(value);
        log.debug("toJson type={}, length={}", typeName(value), json.length());
        return json;
    }

    public static <T> T convert(Object value, Class<T> toType) {
        T converted = mapper().convertValue(value, toType);
        log.debug("convert {} -> {}", typeName(value), toType.getName());
        return converted;
    }

    public static <T> T fromJson(String json, Class<T> type) {
        T value = mapper().readValue(json, type);
        log.debug("fromJson type={}, length={}", type.getName(), json == null ? 0 : json.length());
        return value;
    }

    private static JsonMapper mapper() {
        return JSON_MAPPER.get();
    }

    private static String typeName(Object value) {
        return value == null ? "null" : value.getClass().getName();
    }
}
