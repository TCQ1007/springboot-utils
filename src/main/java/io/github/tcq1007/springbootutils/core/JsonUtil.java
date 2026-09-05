package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@ConditionalOnClass(JsonMapper.class)
public class JsonUtil {

    private static final Lazy<JsonMapper> JSON_MAPPER = SpringBeanUtil.getLazy(JsonMapper.class);

    public static String toJson(Object value) {
        return getMapper().writeValueAsString(value);
    }

    public static <T> T convert(Object value, Class<T> toType) {
        return getMapper().convertValue(value, toType);
    }

    public static <T> T fromJson(String value, Class<T> clazz) {
        return getMapper().readValue(value, clazz);
    }

    private static JsonMapper getMapper() {
        return JSON_MAPPER.get();
    }
}
