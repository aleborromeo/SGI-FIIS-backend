package com.sgi.fiis.shared.infrastructure.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocaleConfig {

    @Bean
    public AcceptHeaderLocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        List<Locale> supportedLocales = Arrays.asList(
                Locale.forLanguageTag("es"),
                Locale.forLanguageTag("en")
        );
        resolver.setSupportedLocales(supportedLocales);
        resolver.setDefaultLocale(Locale.forLanguageTag("es"));
        return resolver;
    }
}
