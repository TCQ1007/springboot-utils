package io.github.tcq1007.springbootutils.localfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.cloud.context.environment.EnvironmentManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBooleanProperty("local.file.refresh.enabled")
public class ConfigFileListener extends FileAlterationListenerAdaptor {

    private final EnvironmentManager environmentManager;

    @Override
    public void onFileCreate(File file) {
        log.info("onFileCreate:{}", file.getAbsolutePath());
    }

    @Override
    public void onFileChange(final File file) {
        // noop
        String absolutePath = file.getAbsolutePath();
        String extension = FilenameUtils.getExtension(absolutePath);
        String baseName = FilenameUtils.getBaseName(absolutePath);
        String fileName = FilenameUtils.getName(absolutePath);

        log.info("onFileChange:{}", file.getAbsolutePath());
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(file));
            Set<String> strings = properties.stringPropertyNames();
            strings.forEach(propertyName -> {
                environmentManager.setProperty(propertyName, properties.getProperty(propertyName));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFileDelete(File directory) {
        log.info("onFileDelete:{}", directory.getAbsolutePath());
    }
}
