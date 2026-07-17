package com.sgi.fiis.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    @SuppressWarnings("java:S4502") // CSRF deshabilitado de forma segura ya que el API es stateless
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                // Deshabilitar CSRF es seguro aquí porque la API es stateless y utiliza tokens JWT
                // enviados en la cabecera 'Authorization: Bearer <token>', no cookies de sesión,
                // lo que elimina el riesgo de ataques Cross-Site Request Forgery (CSRF).
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    // Rutas públicas
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/api/v1/calls/vigent").permitAll()
                    .requestMatchers("/health").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/calls/**").permitAll()
                    // Rutas protegidas por rol
                    .requestMatchers(HttpMethod.GET, "/api/v1/users").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated()
                    .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/roles/**").hasRole("ADMIN")
                    // Cualquier otra petición requiere autenticación
                    .anyRequest().authenticated()
                )
                // JWT filter para endpoints protegidos
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                    .defaultAuthenticationEntryPointFor(
                        new org.springframework.security.web.authentication.HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED),
                        org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.pathPattern("/api/**")
                    )
                );

            return http.build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to configure security filter chain", e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.boot.web.servlet.FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(JwtAuthenticationFilter filter) {
        org.springframework.boot.web.servlet.FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new org.springframework.boot.web.servlet.FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
