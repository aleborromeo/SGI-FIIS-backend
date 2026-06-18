package com.sgi.fiis.shared.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

/**
 * Configuración de Swagger / OpenAPI 3.
 * Configura la información general de la API y añade soporte para el flujo de autorización con JWT Bearer tokens.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @PostConstruct
    public void printMailConfig() {
        System.out.println("=================================================");
        System.out.println("DEBUG MAIL CONFIGURATION ON STARTUP:");
        System.out.println("Host: " + mailHost);
        System.out.println("Username: " + mailUsername);
        System.out.println("Password length: " + (mailPassword != null ? mailPassword.length() : "null"));
        System.out.println("=================================================");
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("SGI FIIS API")
                        .version("1.0.0")
                        .description("Sistema de Gestión de Investigación FIIS - Documentación de Endpoints y Pruebas Interactivas"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
