package io.github.tcq1007.springbootutils.core;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnBooleanProperty("local.file.refresh.enabled")
public class ConfigDataLocationUtil implements SmartInitializingSingleton {
    public static final String SPRING_CONFIG_IMPORT = "spring.config.import";
    public static final String DELIMITER = ",";
    public static final List<ConfigDataLocation> configDataLocationList = new ArrayList<>();

    public static List<ConfigDataLocation> getByPrefix(String prefix) {
        return configDataLocationList.stream().filter(e -> e.hasPrefix(prefix)).toList();
    }

    public static List<ConfigDataLocation> getAll() {
        return configDataLocationList;
    }

    @Override
    public void afterSingletonsInstantiated() {
        String springConfigImport = EnvironmentUtil.getProperty(SPRING_CONFIG_IMPORT);
        if (StringUtils.hasText(springConfigImport)) {
            ConfigDataLocation configDataLocation = ConfigDataLocation.of(springConfigImport);
            configDataLocationList.addAll(Arrays.stream(configDataLocation.split(DELIMITER)).toList());
        }
    }
}
