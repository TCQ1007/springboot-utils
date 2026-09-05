package io.github.tcq1007.springbootutils.localfile;

import io.github.tcq1007.springbootutils.core.ConfigDataLocationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBooleanProperty("local.file.refresh.enabled")
public class LocalFileEnvironmentRefresher implements SmartInitializingSingleton {


    private final ConfigurableEnvironment environment;
    private final ConfigFileListener configFileListener;
    private final ResourceLoader resourceLoader;
    private final ApplicationContext applicationContext;


    @EventListener
    public void evenExecute(EnvironmentChangeEvent env) {
        for (String key : env.getKeys()) {
            log.debug("key changed:{}", key);
        }
    }

    public void initMonitor() throws Exception {

        MutablePropertySources propertySources = environment.getPropertySources();

        propertySources.forEach(propertySource -> {
            String name = propertySource.getName();
            log.info("propertySource:{}", propertySource);
            log.info("{}", propertySource.getSource());
            if (propertySource instanceof OriginTrackedMapPropertySource mapPropertySource) {
                mapPropertySource.getOrigin("");
            }
        });

        // 3. 创建监控器，设置检查间隔（比如5000毫秒）
        FileAlterationMonitor monitor = new FileAlterationMonitor(5000);
        List<ConfigDataLocation> locationList = ConfigDataLocationUtil.getByPrefix(ResourceUtils.FILE_URL_PREFIX);

        Map<String, List<String>> dir2NameListMap = new ConcurrentHashMap<>();

        locationList.forEach(location -> {
            boolean optional = location.isOptional();
            Resource resource = resourceLoader.getResource(location.getValue());
            URL url = null;
            try {
                url = resource.getURL();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (resource.isFile() && resource.exists()) {
                try {
                    resource.getFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            URI locationUri = URI.create(location.getValue());
            String filePath = location.getNonPrefixedValue(ResourceUtils.FILE_URL_PREFIX);
            Path path = Paths.get(filePath);
            File file = path.toFile();
            log.info("spring.config.import: file:{}, scheme:{},  exists:{}, optional:{}", location.getValue(), locationUri.getScheme(), file.exists(), optional);

            // 1. 设置监听目录
//            File configDir = new File("./config");
            // 2. 创建观察者并注册监听器
            String parent = file.getParent();
            dir2NameListMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(FilenameUtils.getName(file.getName()));

        });
        dir2NameListMap.forEach((dir, nameList) -> {
            FileAlterationObserver.Builder builder = FileAlterationObserver.builder();
            builder.setPath(dir);
            builder.setFileFilter(new NameFileFilter(nameList));
            FileAlterationObserver observer = null;
            try {
                observer = builder.get();
                observer.addListener(configFileListener);
                monitor.addObserver(observer);
            } catch (IOException e) {
                log.error("observer:{}", dir, e);
                throw new RuntimeException(e);
            }
        });
        // 4. 启动监控
        if (!dir2NameListMap.isEmpty()) {
            monitor.start();
        }

        log.info("开始监控目录");
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            initMonitor();
        } catch (Exception e) {
            log.error("EnvironmentManagerUtil init failed.", e);
            throw new RuntimeException();
        }
    }
}
