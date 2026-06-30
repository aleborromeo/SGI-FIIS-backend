package com.sgi.fiis;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para verificar la conexión a la base de datos PostgreSQL.
 *
 * PREREQUISITO: La base de datos PostgreSQL debe estar corriendo.
 *   - Opción 1: docker-compose up db_fiis
 *   - Opción 2: PostgreSQL local en localhost:5432 con la BD 'db_fiis_investigacion'
 */
@SpringBootTest
@Disabled("Requiere PostgreSQL corriendo en localhost:5432")
@DisplayName("Pruebas de Conexión a Base de Datos")
@org.junit.jupiter.api.Disabled("Deshabilitado temporalmente porque en CI y test local usamos H2 en memoria, y este test requiere PostgreSQL")
class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("1. DataSource se inyecta correctamente")
    void dataSourceShouldBeInjected() {
        assertNotNull(dataSource, "El DataSource no debería ser null");
    }

    @Test
    @DisplayName("2. JdbcTemplate se inyecta correctamente")
    void jdbcTemplateShouldBeInjected() {
        assertNotNull(jdbcTemplate, "El JdbcTemplate no debería ser null");
    }

    @Test
    @DisplayName("3. Se puede obtener una conexión activa a PostgreSQL")
    void shouldEstablishConnection() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "La conexión no debería ser null");
            assertFalse(connection.isClosed(), "La conexión debería estar abierta");
        }
    }

    @Test
    @DisplayName("4. La base de datos es PostgreSQL")
    void shouldConnectToPostgres() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String dbProductName = metaData.getDatabaseProductName();

            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║  Base de datos: " + dbProductName);
            System.out.println("║  Versión: " + metaData.getDatabaseProductVersion());
            System.out.println("║  URL: " + metaData.getURL());
            System.out.println("║  Usuario: " + metaData.getUserName());
            System.out.println("╚══════════════════════════════════════════╝");

            assertTrue(
                dbProductName.toLowerCase().contains("postgresql"),
                "Debería ser PostgreSQL, pero fue: " + dbProductName
            );
        }
    }

    @Test
    @DisplayName("5. SELECT 1 retorna 1 (query básico funciona)")
    void shouldExecuteSimpleQuery() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        assertNotNull(result, "El resultado de SELECT 1 no debería ser null");
        assertEquals(1, result, "SELECT 1 debería retornar 1");
    }

    @Test
    @DisplayName("6. Se puede obtener la fecha/hora del servidor de BD")
    void shouldGetServerTimestamp() {
        String timestamp = jdbcTemplate.queryForObject(
            "SELECT NOW()::text", String.class
        );

        assertNotNull(timestamp, "El timestamp del servidor no debería ser null");
        assertFalse(timestamp.isEmpty(), "El timestamp no debería estar vacío");

        System.out.println("⏰ Timestamp del servidor PostgreSQL: " + timestamp);
    }

    @Test
    @DisplayName("7. Se puede consultar la versión de PostgreSQL")
    void shouldGetPostgresVersion() {
        String version = jdbcTemplate.queryForObject(
            "SELECT version()", String.class
        );

        assertNotNull(version, "La versión de PostgreSQL no debería ser null");
        assertTrue(
            version.toLowerCase().contains("postgresql"),
            "La versión debería mencionar PostgreSQL"
        );

        System.out.println("🐘 Versión completa: " + version);
    }

    @Test
    @DisplayName("8. Se puede verificar el nombre de la base de datos actual")
    void shouldGetCurrentDatabaseName() {
        String dbName = jdbcTemplate.queryForObject(
            "SELECT current_database()", String.class
        );

        assertNotNull(dbName, "El nombre de la BD no debería ser null");
        assertEquals(
            "db_fiis_investigacion", dbName,
            "El nombre de la BD debería ser 'db_fiis_investigacion'"
        );

    }
}











