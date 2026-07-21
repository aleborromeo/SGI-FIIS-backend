package com.sgi.fiis;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Configuracion base para tests de integracion.
 *
 * Local: usa docker-compose PostgreSQL en port 5433 (ya corriendo).
 * CI (GitHub Actions): Testcontainers levanta PostgreSQL automaticamente.
 *
 * Uso: hacer que la clase de test extienda esta clase.
 */
@SuppressWarnings("resource")
public abstract class TestcontainersConfig {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestcontainersConfig.class);
    private static final PostgreSQLContainer<?> POSTGRES;

    protected TestcontainersConfig() {
        // Constructor protegido para ocultar el constructor publico implicito
    }

    static {
        PostgreSQLContainer<?> container = null;
        try {
            // Intentar usar Testcontainers (funciona en CI con Docker)
            container = new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("db_fiis_investigacion")
                    .withUsername("postgres")
                    .withPassword("test");
            container.start();
            // Ensure container is closed when JVM exits
            Runtime.getRuntime().addShutdownHook(new Thread(container::close));
        } catch (Exception e) {
            // Docker no disponible (local) - usar docker-compose PostgreSQL
            log.info("[Testcontainers] Docker no disponible. Usando docker-compose PostgreSQL en localhost:5433");
        }
        POSTGRES = container;
    }

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        if (POSTGRES != null && POSTGRES.isRunning()) {
            // Testcontainers esta activo - sobreescribir properties
            registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES::getUsername);
            registry.add("spring.datasource.password", POSTGRES::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        }
        // Si Testcontainers no esta activo, se usan las properties del application.yml
    }
}
