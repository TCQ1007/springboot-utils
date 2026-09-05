package io.github.tcq1007.springbootutils.localfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.context.environment.EnvironmentManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnClass({ FileAlterationListenerAdaptor.class, EnvironmentManager.class })
@ConditionalOnBooleanProperty("local.file.refresh.enabled")
public class ConfigFileListener extends FileAlterationListenerAdaptor {

    private final EnvironmentManager environmentManager;

    @Override
    public void onFileCreate(File file) {
        log.info("{}: config file created: {}", getClass().getSimpleName(), file.getAbsolutePath());
        reload(file);
    }

    @Override
    public void onFileChange(File file) {
        log.info("{}: config file changed: {}", getClass().getSimpleName(), file.getAbsolutePath());
        reload(file);
    }

    @Override
    public void onFileDelete(File file) {
        log.warn("config file deleted: {}", file.getAbsolutePath());
    }

    private void reload(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        if (!"properties".equalsIgnoreCase(extension)) {
            log.warn("unsupported config file type: {}, only .properties is supported", file.getAbsolutePath());
            return;
        }
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(file));
            int updated = 0;
            for (String key : properties.stringPropertyNames()) {
                environmentManager.setProperty(key, properties.getProperty(key));
                log.debug("updated property {}", key);
                updated++;
            }
            log.info("{}: reloaded {} propertie(s) from {}", getClass().getSimpleName(), updated, file.getAbsolutePath());
        } catch (IOException e) {
            log.error("failed to reload config file {}", file.getAbsolutePath(), e);
        }
    }
}
