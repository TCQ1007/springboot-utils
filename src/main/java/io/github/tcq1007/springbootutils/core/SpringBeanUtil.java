package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnClass(ApplicationContext.class)
public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    public static <T> T get(Class<T> beanClass) {
        if (applicationContext == null) {
            throw new IllegalStateException("SpringBeanUtil applicationContext isn't initialized.");
        }
        return applicationContext.getBean(beanClass);
    }

    public static <T> Lazy<T> getLazy(Class<T> beanClass) {
        return Lazy.of(() -> SpringBeanUtil.get(beanClass));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtil.applicationContext = applicationContext;
        log.info("SpringBeanUtil applicationContext is initialized.");
    }
}
