package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class I18nMessageUtil implements MessageSourceAware, SmartInitializingSingleton {

    private static MessageSource messageSource;

    public static String getMessage(String code) {
        return getMessage(code, null);
    }

    private static String getMessage(String code, Object @Nullable [] args) {
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            log.info("no message:{}", code);
        }
        return null;
    }

    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        I18nMessageUtil.messageSource = messageSource;
    }

    @Override
    public void afterSingletonsInstantiated() {
        String message = I18nMessageUtil.getMessage("user.login");
        log.info(message);
    }
}
