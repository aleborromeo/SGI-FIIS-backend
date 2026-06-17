package com.sgi.fiis.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("SGI-FIIS: Sistema de Gestión Institucional")
                        .version("1.0.0")
                        .description("Documentación global interactiva de todas las APIs REST del sistema de la facultad (FIIS).")
                        .contact(new Contact()
                                .name("Soporte FIIS")
                                .email("soporte.fiis@unas.edu.pe")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    // --- MÓDULOS DEL CORE ---

    // El Backend Unificado al completo (Auth, Users, Documentos)
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all-apis")
                .pathsToMatch("/api/**")
                .packagesToScan("com.sgi.fiis.auth", "com.sgi.fiis.users", "com.sgi.fiis.documentacion")
                .build();
    }

    // Gestión Documental
    @Bean
    public GroupedOpenApi documentsApi() {
        return GroupedOpenApi.builder()
                .group("documents")
                .pathsToMatch("/api/documents/**")
                .packagesToScan("com.sgi.fiis.documentacion")
                .build();
    }

    // Autenticación, Roles y Usuarios
    @Bean
    public GroupedOpenApi authAndUsersApi() {
        return GroupedOpenApi.builder()
                .group("auth-users")
                .pathsToMatch("/api/auth/**", "/api/users/**")
                .packagesToScan("com.sgi.fiis.auth", "com.sgi.fiis.users")
                .build();
    }

    // Grupos y Líneas de Investigación
    @Bean
    public GroupedOpenApi researchApi() {
        return GroupedOpenApi.builder()
                .group("research")
                .pathsToMatch("/api/researchgroups/**", "/api/researchlines/**")
                .build();
    }

    // Convocatorias y Proyectos de Investigación
    @Bean
    public GroupedOpenApi projectsApi() {
        return GroupedOpenApi.builder()
                .group("projects")
                .pathsToMatch("/api/convocatorias/**", "/api/projects/**")
                .build();
    }

    // Flujo de Trámites
    @Bean
    public GroupedOpenApi tramitesApi() {
        return GroupedOpenApi.builder()
                .group("tramites")
                .pathsToMatch("/api/tramites/**")
                .build();
    }

    // Planes de Tesis
    @Bean
    public GroupedOpenApi thesisApi() {
        return GroupedOpenApi.builder()
                .group("thesis")
                .pathsToMatch("/api/thesis/**")
                .build();
    }

    // Informes de Avance
    @Bean
    public GroupedOpenApi progressReportsApi() {
        return GroupedOpenApi.builder()
                .group("progress-reports")
                .pathsToMatch("/api/progressreports/**")
                .build();
    }

    // Resoluciones
    @Bean
    public GroupedOpenApi resolutionsApi() {
        return GroupedOpenApi.builder()
                .group("resolutions")
                .pathsToMatch("/api/resolutions/**")
                .build();
    }

    // Evaluaciones
    @Bean
    public GroupedOpenApi evaluationsApi() {
        return GroupedOpenApi.builder()
                .group("evaluations")
                .pathsToMatch("/api/evaluations/**")
                .build();
    }

    // Observaciones y Subsanaciones
    @Bean
    public GroupedOpenApi observationsApi() {
        return GroupedOpenApi.builder()
                .group("observations")
                .pathsToMatch("/api/observations/**", "/api/observaciones/**")
                .build();
    }

    // Dashboards por Rol
    @Bean
    public GroupedOpenApi dashboardsApi() {
        return GroupedOpenApi.builder()
                .group("dashboards")
                .pathsToMatch("/api/dashboards/**", "/api/dashboard/**")
                .build();
    }

    // Reportes y Auditoría
    @Bean
    public GroupedOpenApi reportsApi() {
        return GroupedOpenApi.builder()
                .group("reports")
                .pathsToMatch("/api/reports/**")
                .build();
    }
}