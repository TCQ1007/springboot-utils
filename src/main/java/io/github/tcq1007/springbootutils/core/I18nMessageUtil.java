package io.github.tcq1007.springbootutils.core;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
public class I18nMessageUtil implements MessageSourceAware {

    private static MessageSource messageSource;

    public static @Nullable String getMessage(String code) {
        return getMessage(code, (Object[]) null);
    }

    public static @Nullable String getMessage(String code, Object @Nullable ... args) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            String message = requireMessageSource().getMessage(code, args, locale);
            log.debug("resolved message code={}, locale={}", code, locale);
            return message;
        } catch (NoSuchMessageException e) {
            log.debug("no message for code={}, locale={}", code, locale);
            return null;
        }
    }

    private static MessageSource requireMessageSource() {
        if (messageSource == null) {
            throw new IllegalStateException("I18nMessageUtil is not initialized");
        }
        return messageSource;
    }

    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        I18nMessageUtil.messageSource = messageSource;
        log.info("{} initialized", getClass().getSimpleName());
    }
}
