package com.sgi.fiis.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
        // Provide mock values to avoid null pointer issues during logging, if any
        ReflectionTestUtils.setField(openApiConfig, "mailHost", "smtp.test.com");
        ReflectionTestUtils.setField(openApiConfig, "mailUsername", "testuser");
        ReflectionTestUtils.setField(openApiConfig, "mailPassword", "testpass");
        ReflectionTestUtils.setField(openApiConfig, "mailMock", true);
    }

    @Test
    void printMailConfig_shouldExecuteWithoutErrors() {
        assertDoesNotThrow(() -> openApiConfig.printMailConfig());
    }

    @Test
    void customOpenAPI_shouldReturnConfiguredOpenAPI() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("SGI FIIS API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        
        // Assert security items
        assertNotNull(openAPI.getSecurity());
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        assertNotNull(openAPI.getComponents().getSecuritySchemes().get("bearerAuth"));
    }

    @Test
    void allApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.allApi();
        assertNotNull(api);
        assertEquals("all-apis", api.getGroup());
    }

    @Test
    void documentsApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.documentsApi();
        assertNotNull(api);
        assertEquals("documents", api.getGroup());
    }

    @Test
    void authAndUsersApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.authAndUsersApi();
        assertNotNull(api);
        assertEquals("auth-users", api.getGroup());
    }

    @Test
    void researchApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.researchApi();
        assertNotNull(api);
        assertEquals("research", api.getGroup());
    }

    @Test
    void projectsApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.projectsApi();
        assertNotNull(api);
        assertEquals("projects", api.getGroup());
    }

    @Test
    void proceduresApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.proceduresApi();
        assertNotNull(api);
        assertEquals("procedures", api.getGroup());
    }

    @Test
    void thesisApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.thesisApi();
        assertNotNull(api);
        assertEquals("thesis", api.getGroup());
    }

    @Test
    void progressReportsApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.progressReportsApi();
        assertNotNull(api);
        assertEquals("progress-reports", api.getGroup());
    }

    @Test
    void resolutionsApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.resolutionsApi();
        assertNotNull(api);
        assertEquals("resolutions", api.getGroup());
    }

    @Test
    void evaluationsApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.evaluationsApi();
        assertNotNull(api);
        assertEquals("evaluations", api.getGroup());
    }

    @Test
    void observationsApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.observationsApi();
        assertNotNull(api);
        assertEquals("observations", api.getGroup());
    }

    @Test
    void dashboardsApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.dashboardsApi();
        assertNotNull(api);
        assertEquals("dashboards", api.getGroup());
    }

    @Test
    void reportsApi_shouldReturnConfiguredGroup() {
        GroupedOpenApi api = openApiConfig.reportsApi();
        assertNotNull(api);
        assertEquals("reports", api.getGroup());
    }
}
