package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@ConditionalOnBooleanProperty("local.file.refresh.enabled")
public class ConfigDataLocationUtil implements SmartInitializingSingleton {

    public static final String SPRING_CONFIG_IMPORT = "spring.config.import";
    public static final String DELIMITER = ",";

    private static final List<ConfigDataLocation> LOCATIONS = new ArrayList<>();

    public static List<ConfigDataLocation> getByPrefix(String prefix) {
        return getAll().stream().filter(location -> location.hasPrefix(prefix)).toList();
    }

    public static List<ConfigDataLocation> getAll() {
        return List.copyOf(LOCATIONS);
    }

    @Override
    public void afterSingletonsInstantiated() {
        String springConfigImport = EnvironmentUtil.getProperty(SPRING_CONFIG_IMPORT);
        if (!StringUtils.hasText(springConfigImport)) {
            log.info("{}: no {} configured", getClass().getSimpleName(), SPRING_CONFIG_IMPORT);
            return;
        }
        LOCATIONS.addAll(Arrays.asList(ConfigDataLocation.of(springConfigImport).split(DELIMITER)));
        log.info("{} loaded {} location(s) from {}: {}", getClass().getSimpleName(),
                LOCATIONS.size(), SPRING_CONFIG_IMPORT, LOCATIONS);
    }
}
