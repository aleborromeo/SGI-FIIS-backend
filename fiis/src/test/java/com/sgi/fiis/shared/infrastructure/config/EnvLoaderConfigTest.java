package com.sgi.fiis.shared.infrastructure.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnvLoaderConfigTest {

    private Path envFile;

    @BeforeEach
    void setUp() throws IOException {
        envFile = Paths.get(".env");
        if (Files.exists(envFile)) {
            Files.delete(envFile);
        }
        System.clearProperty("AZURE_CLIENT_ID");
        System.clearProperty("AZURE_CLIENT_SECRET");
        System.clearProperty("TEST_VAR");
        System.clearProperty("TEST_PASSWORD");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(envFile)) {
            Files.delete(envFile);
        }
        System.setProperty("AZURE_CLIENT_ID", "dummy-client-id");
        System.setProperty("AZURE_CLIENT_SECRET", "dummy-client-secret");
        System.clearProperty("TEST_VAR");
        System.clearProperty("TEST_PASSWORD");
    }

    @Test
    void testLoadEnv_NoFile_SetsAzureDefaults() {
        EnvLoaderConfig.loadEnv();
        
        String envClientId = System.getenv("AZURE_CLIENT_ID");
        if (envClientId == null || envClientId.trim().isEmpty()) {
            assertNotNull(System.getProperty("AZURE_CLIENT_ID"));
            assertEquals("dummy-client-id", System.getProperty("AZURE_CLIENT_ID"));
        }

        String envClientSecret = System.getenv("AZURE_CLIENT_SECRET");
        if (envClientSecret == null || envClientSecret.trim().isEmpty()) {
            assertNotNull(System.getProperty("AZURE_CLIENT_SECRET"));
            assertEquals("dummy-client-secret", System.getProperty("AZURE_CLIENT_SECRET"));
        }
    }

    @Test
    void testLoadEnv_WithFile_SetsVariables() throws IOException {
        List<String> lines = List.of(
                "TEST_VAR=my_value",
                "TEST_PASSWORD='secret_password'",
                "# This is a comment",
                "INVALID_LINE"
        );
        Files.write(envFile, lines);

        EnvLoaderConfig.loadEnv();

        assertEquals("my_value", System.getProperty("TEST_VAR"));
        assertEquals("secret_password", System.getProperty("TEST_PASSWORD"));
    }
}
