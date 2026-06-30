package com.sgi.fiis.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String RESEARCH_GROUPS_PATH = "/api/v1/research-groups/**";
    private static final String RESEARCH_LINES_PATH = "/api/v1/research-lines/**";

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          CustomAuthenticationEntryPoint customAuthEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.customAuthEntryPoint = customAuthEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
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
                    .requestMatchers("/health").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    // Rutas protegidas por rol
                    .requestMatchers("/api/v1/users/**").hasRole(ROLE_ADMIN)
                    .requestMatchers("/api/v1/roles/**").hasRole(ROLE_ADMIN)
                    .requestMatchers(org.springframework.http.HttpMethod.POST, RESEARCH_GROUPS_PATH).hasRole(ROLE_ADMIN)
                    .requestMatchers(org.springframework.http.HttpMethod.PATCH, RESEARCH_GROUPS_PATH).hasRole(ROLE_ADMIN)
                    .requestMatchers(org.springframework.http.HttpMethod.DELETE, RESEARCH_GROUPS_PATH).hasRole(ROLE_ADMIN)
                    .requestMatchers(org.springframework.http.HttpMethod.POST, RESEARCH_LINES_PATH).hasRole(ROLE_ADMIN)
                    .requestMatchers(org.springframework.http.HttpMethod.PATCH, RESEARCH_LINES_PATH).hasRole(ROLE_ADMIN)
                    .requestMatchers(org.springframework.http.HttpMethod.DELETE, RESEARCH_LINES_PATH).hasRole(ROLE_ADMIN)
                    // Dashboard security rules (RF-88 a RF-94)
                    .requestMatchers("/api/v1/dashboard/me").authenticated()
                    .requestMatchers("/api/v1/dashboard/admin/**").hasRole(ROLE_ADMIN)
                    .requestMatchers("/api/v1/dashboard/director/**").hasRole("DIRECTOR_INVESTIGACION")
                    .requestMatchers("/api/v1/dashboard/coordinator/**").hasRole("COORDINADOR_GRUPO")
                    .requestMatchers("/api/v1/dashboard/teacher/**").hasRole("DOCENTE_INVESTIGADOR")
                    .requestMatchers("/api/v1/dashboard/evaluator/**").hasRole("EVALUADOR")
                    .requestMatchers("/api/v1/dashboard/dean/**").hasRole("DECANO")
                    .requestMatchers("/api/v1/dashboard/student/**").hasRole("ESTUDIANTE")
                    // Cualquier otra petición requiere autenticación
                    .anyRequest().authenticated()
                )
                // JWT filter para endpoints protegidos
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler)
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
