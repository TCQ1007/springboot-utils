package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import nl.talsmasoftware.lazy4j.Lazy;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnClass(ApplicationContext.class)
public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T get(Class<T> beanClass) {
        return requireContext().getBean(beanClass);
    }

    public static <T> Lazy<T> getLazy(Class<T> beanClass) {
        return Lazy.of(() -> get(beanClass));
    }

    private static ApplicationContext requireContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("SpringBeanUtil is not initialized");
        }
        return applicationContext;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtil.applicationContext = applicationContext;
        log.info("{} initialized, id={}", getClass().getSimpleName(), applicationContext.getId());
    }
}
