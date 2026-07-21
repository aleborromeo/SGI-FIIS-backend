package com.sgi.fiis.shared.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("all")
class LocaleConfigTest {

    private final LocaleConfig localeConfig = new LocaleConfig();

    @Test
    void localeResolver_shouldReturnConfiguredAcceptHeaderLocaleResolver() {
        LocaleResolver resolver = localeConfig.localeResolver();

        assertNotNull(resolver);
        assertTrue(resolver instanceof AcceptHeaderLocaleResolver);

        AcceptHeaderLocaleResolver acceptHeaderResolver = (AcceptHeaderLocaleResolver) resolver;

        assertNotNull(acceptHeaderResolver.getSupportedLocales());
        assertEquals(2, acceptHeaderResolver.getSupportedLocales().size());
        assertTrue(acceptHeaderResolver.getSupportedLocales().contains(new Locale("es")));
        assertTrue(acceptHeaderResolver.getSupportedLocales().contains(new Locale("en")));
    }
}
