package com.sgi.fiis.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {SwaggerSecurityConfig.class})
class SwaggerSecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void localSwaggerSecurityFilterChain_shouldReturnSecurityFilterChain() {
        // Assert that the context loads and the bean is created successfully
        SecurityFilterChain filterChain = context.getBean(SecurityFilterChain.class);
        assertNotNull(filterChain);
        // The bean name is localSwaggerSecurityFilterChain
        assertTrue(context.containsBean("localSwaggerSecurityFilterChain"));
    }
}
