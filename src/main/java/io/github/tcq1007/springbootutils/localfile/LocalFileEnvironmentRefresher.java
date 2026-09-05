package io.github.tcq1007.springbootutils.localfile;

import io.github.tcq1007.springbootutils.core.ConfigDataLocationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnClass({ FileAlterationMonitor.class, EnvironmentChangeEvent.class })
@ConditionalOnBooleanProperty("local.file.refresh.enabled")
public class LocalFileEnvironmentRefresher implements SmartInitializingSingleton, DisposableBean {

    private static final long POLL_INTERVAL_MS = 5_000L;

    private final ConfigFileListener configFileListener;

    private FileAlterationMonitor monitor;

    @EventListener
    public void onEnvironmentChange(EnvironmentChangeEvent event) {
        log.info("{}: environment changed, keys={}", getClass().getSimpleName(), event.getKeys());
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            startMonitor();
        } catch (Exception e) {
            throw new IllegalStateException("failed to start local config file monitor", e);
        }
    }

    @Override
    public void destroy() {
        if (monitor == null) {
            return;
        }
        try {
            monitor.stop();
            log.info("{}: stopped local config file monitor", getClass().getSimpleName());
        } catch (Exception e) {
            log.warn("failed to stop local config file monitor", e);
        }
    }

    private void startMonitor() throws Exception {
        List<ConfigDataLocation> locations = ConfigDataLocationUtil.getByPrefix(ResourceUtils.FILE_URL_PREFIX);
        if (locations.isEmpty()) {
            log.info("{}: no file: locations in spring.config.import, skip monitor", getClass().getSimpleName());
            return;
        }

        Map<String, List<String>> dirToFileNames = new LinkedHashMap<>();
        for (ConfigDataLocation location : locations) {
            collectWatchTarget(location, dirToFileNames);
        }
        if (dirToFileNames.isEmpty()) {
            log.warn("no watchable local config file found");
            return;
        }

        monitor = new FileAlterationMonitor(POLL_INTERVAL_MS);
        for (Map.Entry<String, List<String>> entry : dirToFileNames.entrySet()) {
            FileAlterationObserver observer = FileAlterationObserver.builder()
                    .setPath(entry.getKey())
                    .setFileFilter(new NameFileFilter(entry.getValue()))
                    .get();
            observer.addListener(configFileListener);
            monitor.addObserver(observer);
            log.info("{}: watching dir={}, files={}", getClass().getSimpleName(), entry.getKey(), entry.getValue());
        }
        monitor.start();
        log.info("{}: started local config file monitor, interval={}ms, dirs={}",
                getClass().getSimpleName(), POLL_INTERVAL_MS, dirToFileNames.size());
    }

    private void collectWatchTarget(ConfigDataLocation location, Map<String, List<String>> dirToFileNames) {
        String filePath = location.getNonPrefixedValue(ResourceUtils.FILE_URL_PREFIX);
        File file = Paths.get(filePath).toFile();
        Path parent = file.toPath().getParent();
        if (parent == null) {
            log.warn("skip location without parent dir: {}", location.getValue());
            return;
        }
        if (!file.exists() && !location.isOptional()) {
            log.warn("config file does not exist: {}", file.getAbsolutePath());
        }
        log.debug("watch candidate location={}, exists={}, optional={}",
                location.getValue(), file.exists(), location.isOptional());
        dirToFileNames.computeIfAbsent(parent.toString(), _ -> new ArrayList<>())
                .add(FilenameUtils.getName(file.getName()));
    }
}
