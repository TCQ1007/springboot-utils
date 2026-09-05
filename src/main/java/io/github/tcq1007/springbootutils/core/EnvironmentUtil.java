package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvironmentUtil implements EnvironmentAware {

    public static Environment ENV;

    public static String getProperty(String property) {
        return ENV.getProperty(property);
    }

    public static boolean containsProperty(String key) {
        return ENV.containsProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return ENV.getProperty(key, defaultValue);
    }

    public static @Nullable <T> T getProperty(String key, Class<T> targetType) {
        return ENV.getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return ENV.getProperty(key, targetType, defaultValue);
    }

    public static String getRequiredProperty(String key) throws IllegalStateException {
        return ENV.getRequiredProperty(key);
    }

    public static <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        return ENV.getRequiredProperty(key, targetType);
    }

    public static String resolvePlaceholders(String text) {
        return ENV.resolvePlaceholders(text);
    }

    public static String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return ENV.resolveRequiredPlaceholders(text);
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        EnvironmentUtil.ENV = environment;
    }
}
