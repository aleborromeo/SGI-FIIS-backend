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
public abstract class TestcontainersConfig {

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgres;

    private static final boolean useTestcontainers;

    static {
        PostgreSQLContainer<?> container = null;
        boolean success = false;
        try {
            container = new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("db_fiis_investigacion")
                    .withUsername("postgres")
                    .withPassword("test");
            container.start();
            Runtime.getRuntime().addShutdownHook(new Thread(container::close));
            success = true;
        } catch (Exception e) {
            System.out.println("[Testcontainers] Docker no disponible. Usando docker-compose PostgreSQL en localhost:5433");
        }
        postgres = container;
        useTestcontainers = success;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (useTestcontainers && postgres != null && postgres.isRunning()) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        } else {
            registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5433/db_fiis_investigacion");
            registry.add("spring.datasource.username", () -> "postgres");
            registry.add("spring.datasource.password", () -> "1tesla");
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        }
    }
}
