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

    private static Environment environment;

    public static boolean containsProperty(String key) {
        return requireEnvironment().containsProperty(key);
    }

    public static String getProperty(String key) {
        return requireEnvironment().getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return requireEnvironment().getProperty(key, defaultValue);
    }

    public static @Nullable <T> T getProperty(String key, Class<T> targetType) {
        return requireEnvironment().getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return requireEnvironment().getProperty(key, targetType, defaultValue);
    }

    public static String getRequiredProperty(String key) {
        return requireEnvironment().getRequiredProperty(key);
    }

    public static <T> T getRequiredProperty(String key, Class<T> targetType) {
        return requireEnvironment().getRequiredProperty(key, targetType);
    }

    public static String resolvePlaceholders(String text) {
        return requireEnvironment().resolvePlaceholders(text);
    }

    public static String resolveRequiredPlaceholders(String text) {
        return requireEnvironment().resolveRequiredPlaceholders(text);
    }

    private static Environment requireEnvironment() {
        if (environment == null) {
            throw new IllegalStateException("EnvironmentUtil is not initialized");
        }
        return environment;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        EnvironmentUtil.environment = environment;
        log.info("{} initialized, activeProfiles={}", getClass().getSimpleName(),
                String.join(",", environment.getActiveProfiles()));
    }
}
