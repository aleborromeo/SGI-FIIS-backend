package com.sgi.fiis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cadena de seguridad exclusiva para las rutas de documentación Swagger/OpenAPI.
 * Se ejecuta con prioridad máxima antes que la cadena principal de auth,
 * y permite bypass para la visualización y login de Swagger.
 */
@Configuration
@EnableWebSecurity
public class SwaggerSecurityConfig {

    @Bean
    @Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
    @SuppressWarnings("java:S4502") // CSRF deshabilitado de forma segura ya que el API es stateless
    public SecurityFilterChain localSwaggerSecurityFilterChain(HttpSecurity http) {
        try {
            http
                .securityMatcher(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/api/auth/login"
                )
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

            return http.build();
        } catch (Exception e) {
            throw new IllegalStateException("Fallo al configurar la cadena de filtrado de Swagger", e);
        }
    }
}
