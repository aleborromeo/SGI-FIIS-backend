package com.sgi.fiis.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Cargador manual de variables de entorno desde el archivo .env.
 * Garantiza que las propiedades estén disponibles en el System antes de que Spring resuelva los placeholders.
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EnvLoaderConfig {

    static {
        try {
            // Buscar .env en el directorio actual o en el directorio padre
            java.nio.file.Path path = java.nio.file.Paths.get(".env");
            if (!java.nio.file.Files.exists(path)) {
                path = java.nio.file.Paths.get("../.env");
            }
            if (!java.nio.file.Files.exists(path)) {
                // Buscar en el directorio actual dentro de fiis (por si se arranca desde la raíz del workspace)
                path = java.nio.file.Paths.get("fiis/.env");
            }

            if (java.nio.file.Files.exists(path)) {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(path);
                int count = 0;
                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String val = parts[1].trim();
                        if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
                            val = val.substring(1, val.length() - 1);
                        } else if (val.startsWith("'") && val.endsWith("'") && val.length() >= 2) {
                            val = val.substring(1, val.length() - 1);
                        }
                        System.setProperty(key, val);
                        count++;
                    }
                }
                System.out.println("=================================================");
                System.out.println("[ENV LOADER] Carga manual de " + count + " variables desde " + path.toAbsolutePath());
                System.out.println("=================================================");
            } else {
                System.out.println("=================================================");
                System.out.println("[ENV LOADER] No se encontró el archivo .env en ninguna ruta.");
                System.out.println("=================================================");
            }
        } catch (Exception e) {
            System.err.println("[ENV LOADER] Error cargando .env: " + e.getMessage());
        }
    }
}
