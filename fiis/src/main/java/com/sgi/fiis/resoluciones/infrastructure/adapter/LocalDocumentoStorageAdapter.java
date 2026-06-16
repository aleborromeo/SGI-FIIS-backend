package com.sgi.fiis.resoluciones.infrastructure.adapter;

import com.sgi.fiis.resoluciones.domain.port.out.DocumentoStoragePort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class LocalDocumentoStorageAdapter implements DocumentoStoragePort {

    private final JdbcTemplate jdbcTemplate;

    public LocalDocumentoStorageAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long guardarDocumento(byte[] archivoBytes, String nombreArchivo, String tipoContenido) {
        // En una implementación real, aquí se guarda el archivo en S3, disco duro o MinIO.
        // Como estamos haciendo un stub hasta que M5 (documentación) esté listo,
        // simularemos el guardado e insertaremos directamente en la tabla documentos
        // para satisfacer la llave foránea de PostgreSQL.
        
        String rutaSimulada = "/storage/resoluciones/" + System.currentTimeMillis() + "_" + nombreArchivo;
        long tamanoBytes = archivoBytes != null ? archivoBytes.length : 0;
        
        // id_usuario_subida = 1 (simulando que el Decano es el usuario ID 1 por ahora)
        String sql = "INSERT INTO documentos (nombre_original, nombre_almacenamiento, ruta_archivo, " +
                     "tipo_contenido, tamano_bytes, id_usuario_subida) VALUES (?, ?, ?, ?, ?, ?)";
                     
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nombreArchivo);
            ps.setString(2, "STUB_" + nombreArchivo);
            ps.setString(3, rutaSimulada);
            ps.setString(4, tipoContenido != null ? tipoContenido : "application/pdf");
            ps.setLong(5, tamanoBytes);
            ps.setLong(6, 1L); // Asumiendo que el Decano / Administrador tiene ID 1
            return ps;
        }, keyHolder);
        
        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id_documento")) {
            return ((Number) keyHolder.getKeys().get("id_documento")).longValue();
        }
        throw new IllegalStateException("Fallo al insertar documento simulado y obtener su ID.");
    }
}
