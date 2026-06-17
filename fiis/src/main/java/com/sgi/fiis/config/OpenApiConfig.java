package com.sgi.fiis.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API del proyecto SGI-FIIS
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SGI FIIS API")
                        .description("Sistema de Gestión de Investigación FIIS - API REST")
                        .version("v1.0.0")
                        .contact(new Contact()
                            .name("Equipo SGI FIIS")
                            .email("investigacion.fiis@sgi.com"))
                        .license(new License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                    new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Servidor de desarrollo local")
                ));
    }

    @Bean
    public GroupedOpenApi observacionesApi() {
        return GroupedOpenApi.builder()
                .group("Módulo Observaciones")
                .pathsToMatch("/api/observaciones/**")
                .build();
    }

    @Bean
    public GroupedOpenApi healthApi() {
        return GroupedOpenApi.builder()
                .group("Módulo General/Health")
                .pathsToMatch("/health")
                .build();
    }
}