package com.sgi.fiis.resolutions.infrastructure.adapter;

import com.sgi.fiis.resolutions.domain.port.out.DocumentStoragePort;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class LocalDocumentStorageAdapter implements DocumentStoragePort {

    private final JdbcTemplate jdbcTemplate;
    private final MessageSource messageSource;

    public LocalDocumentStorageAdapter(JdbcTemplate jdbcTemplate, MessageSource messageSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.messageSource = messageSource;
    }

    @Override
    public Long saveDocument(byte[] fileBytes, String fileName, String contentType) {
        // In a real implementation, the file would be saved to S3, disk or MinIO.
        // As a stub until M5 (documentation) is ready,
        // we simulate the save and insert directly into the documentos table
        // to satisfy the PostgreSQL foreign key constraint.

        String simulatedPath = "/storage/resoluciones/" + System.currentTimeMillis() + "_" + fileName;
        long sizeBytes = fileBytes != null ? fileBytes.length : 0;

        // id_usuario_subida = 1 (simulating that the Dean is user ID 1 for now)
        String sql = "INSERT INTO documentos (nombre_original, ruta_almacenamiento, " +
                     "tipo_extension, tamano_bytes, id_usuario_subio) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, fileName);
            ps.setString(2, simulatedPath);
            ps.setString(3, "pdf"); // Stub: assuming short extension
            ps.setLong(4, sizeBytes);
            ps.setLong(5, 1L); // Assuming the Dean / Administrator has ID 1
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id_documento")) {
            return ((Number) keyHolder.getKeys().get("id_documento")).longValue();
        }
        throw new IllegalStateException(
                messageSource.getMessage("resolution.error.document-save-failed", null, LocaleContextHolder.getLocale()));
    }
}
