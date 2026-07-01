package com.sgi.fiis.shared.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger / OpenAPI 3.
 * Configura la información general de la API y añade soporte para el flujo de autorización con JWT Bearer tokens.
 */
@Configuration
@lombok.extern.slf4j.Slf4j
public class OpenApiConfig {

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${app.mail.mock:true}")
    private boolean mailMock;
    @PostConstruct
    public void printMailConfig() {
        log.info("=================================================");
        log.info("DEBUG MAIL CONFIGURATION ON STARTUP:");
        log.info("Host: {}", mailHost);
        log.info("Username: {}", mailUsername);
        log.info("Password length: {}", (mailPassword != null ? mailPassword.length() : "null"));
        log.info("Mail Mode: {}", (mailMock ? "MOCK (Fake Sender)" : "SMTP (Real Sender)"));
        log.info("=================================================");
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

    // --- MÓDULOS DEL CORE ---

    // El Backend Unificado al completo (Auth, Users, Documentos)
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all-apis")
                .pathsToMatch("/api/**")
                .packagesToScan("com.sgi.fiis.auth", "com.sgi.fiis.users", "com.sgi.fiis.documentacion",
                        "com.sgi.fiis.tramites", "com.sgi.fiis.grupos_investigacion",
                        "com.sgi.fiis.lineas_investigacion", "com.sgi.fiis.dashboards",
                        "com.sgi.fiis.reportes", "com.sgi.fiis.observations", "com.sgi.fiis.thesis")
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
                .pathsToMatch("/api/v1/auth/**", "/api/v1/users/**", "/api/v1/roles/**")
                .packagesToScan("com.sgi.fiis.auth", "com.sgi.fiis.users")
                .build();
    }

    // Grupos y Líneas de Investigación
    @Bean
    public GroupedOpenApi researchApi() {
        return GroupedOpenApi.builder()
                .group("research")
                .pathsToMatch("/api/v1/research-groups/**", "/api/v1/research-lines/**")
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

    // Procedures (Academic Workflow)
    @Bean
    public GroupedOpenApi proceduresApi() {
        return GroupedOpenApi.builder()
                .group("procedures")
                .pathsToMatch("/api/v1/procedures/**")
                .packagesToScan("com.sgi.fiis.tramites")
                .build();
    }

    // Planes de Tesis e Informes de Tesis
    @Bean
    public GroupedOpenApi thesisApi() {
        return GroupedOpenApi.builder()
                .group("thesis")
                .pathsToMatch("/api/v1/thesis/**")
                .packagesToScan("com.sgi.fiis.thesis")
                .build();
    }

    // Informes de Avance
    @Bean
    public GroupedOpenApi progressReportsApi() {
        return GroupedOpenApi.builder()
                .group("progress-reports")
                .pathsToMatch("/api/progress-reports/**")
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

    // Observations and Remedies
    @Bean
    public GroupedOpenApi observationsApi() {
        return GroupedOpenApi.builder()
                .group("observations")
                .pathsToMatch("/api/observations/**")
                .build();
    }

    // Dashboards por Rol
    @Bean
    public GroupedOpenApi dashboardsApi() {
        return GroupedOpenApi.builder()
                .group("dashboards")
                .pathsToMatch("/api/v1/dashboard/**")
                .build();
    }

    // Reportes y Auditoría
    @Bean
    public GroupedOpenApi reportsApi() {
        return GroupedOpenApi.builder()
                .group("reports")
                .pathsToMatch("/api/reportes/**")
                .build();
    }
}
