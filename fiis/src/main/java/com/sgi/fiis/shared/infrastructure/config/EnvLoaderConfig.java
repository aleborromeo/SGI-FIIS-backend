package com.sgi.fiis.shared.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Cargador manual de variables de entorno desde el archivo .env.
 * Garantiza que las propiedades estén disponibles en el System antes de que Spring resuelva los placeholders.
 */
@Configuration(proxyBeanMethods = false)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EnvLoaderConfig {

    private static final Logger log = LoggerFactory.getLogger(EnvLoaderConfig.class);
    private static final String SEPARATOR = "=================================================";

    private EnvLoaderConfig() {
        // Utility/Configuration class should not be instantiated
    }

    static {
        loadEnv();
    }
    
    public static void loadEnv() {
        try {
            setDummyAzureEnvVars();

            Path path = findEnvPath();
            if (path != null && Files.exists(path)) {
                int count = processEnvFile(path);
                log.info(SEPARATOR);
                log.info("[ENV LOADER] Carga manual de {} variables desde {}", count, path.toAbsolutePath());
                log.info(SEPARATOR);
            } else {
                log.info(SEPARATOR);
                log.info("[ENV LOADER] No se encontró el archivo .env en ninguna ruta.");
                log.info(SEPARATOR);
            }
        } catch (Exception e) {
            log.error("[ENV LOADER] Error cargando .env: {}", e.getMessage());
        }
    }

    private static void setDummyAzureEnvVars() {
        String azureClientId = System.getenv("AZURE_CLIENT_ID");
        if (azureClientId == null || azureClientId.trim().isEmpty()) {
            System.setProperty("AZURE_CLIENT_ID", "dummy-client-id");
        }
        String azureClientSecret = System.getenv("AZURE_CLIENT_SECRET");
        if (azureClientSecret == null || azureClientSecret.trim().isEmpty()) {
            System.setProperty("AZURE_CLIENT_SECRET", "dummy-client-secret");
        }
    }

    private static Path findEnvPath() {
        Path path = Paths.get(".env");
        if (!Files.exists(path)) {
            path = Paths.get("../.env");
        }
        if (!Files.exists(path)) {
            path = Paths.get("fiis/.env");
        }
        return path;
    }

    private static int processEnvFile(Path path) throws java.io.IOException {
        List<String> lines = Files.readAllLines(path);
        int count = 0;
        for (String line : lines) {
            if (parseAndSetEnvVariable(line)) {
                count++;
            }
        }
        return count;
    }

    private static boolean parseAndSetEnvVariable(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
            return false;
        }

        String[] parts = trimmedLine.split("=", 2);
        if (parts.length != 2) {
            return false;
        }

        String key = parts[0].trim();
        String val = cleanEnvValue(parts[1].trim());

        if (!val.isEmpty() || key.toLowerCase().contains("password")) {
            System.setProperty(key, val);
            return true;
        }
        return false;
    }

    private static String cleanEnvValue(String val) {
        if (((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) && val.length() >= 2) {
            return val.substring(1, val.length() - 1);
        }
        return val;
    }
}
