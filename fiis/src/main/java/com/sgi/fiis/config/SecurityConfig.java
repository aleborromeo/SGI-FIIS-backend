package com.sgi.fiis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactiva CSRF de forma categórica usando la sintaxis de referencia moderna
            .csrf(AbstractHttpConfigurer::disable)
            
            // Abre las compuertas de autorización para tus pruebas locales
            .authorizeHttpRequests(auth -> auth
                // 1. Recursos estáticos e internos esenciales de Swagger UI / OpenAPI
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                
                // 2. Endpoints del módulo de documentos abiertos temporalmente
                .requestMatchers("/api/documents/**").permitAll()
                
                // Cualquier otra petición del sistema seguirá requiriendo inicio de sesión
                .anyRequest().authenticated()
            )
            
            // Deshabilita los filtros por defecto de redirección visual y login básico
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}