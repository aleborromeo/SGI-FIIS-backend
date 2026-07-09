package com.sgi.fiis.resolutions.infrastructure.adapter;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.resolutions.domain.port.out.DocumentStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

@Component
public class LocalDocumentStorageAdapter implements DocumentStoragePort {

    private final JdbcTemplate jdbcTemplate;
    private final MessageSource messageSource;
    private final Path rootLocation;

    public LocalDocumentStorageAdapter(
            JdbcTemplate jdbcTemplate,
            MessageSource messageSource,
            @Value("${storage.local.root:uploads-fiis}") String rootDir) {
        this.jdbcTemplate = jdbcTemplate;
        this.messageSource = messageSource;
        this.rootLocation = Paths.get(rootDir);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo inicializar la carpeta de almacenamiento local de la FIIS", e);
        }
    }

    @Override
    public Long saveDocument(byte[] fileBytes, String fileName, String contentType) {
        if (fileName == null || fileName.contains("..") || fileName.isEmpty()) {
            throw new IllegalArgumentException("Nombre de archivo no válido o intento de Path Traversal.");
        }

        String cleanedFileName = Paths.get(fileName).getFileName().toString();
        String uniqueName = UUID.randomUUID().toString() + "_" + cleanedFileName;
        Path destinationFile = this.rootLocation.resolve(uniqueName).normalize().toAbsolutePath();

        if (!destinationFile.startsWith(this.rootLocation.toAbsolutePath())) {
            throw new IllegalArgumentException("Nombre de archivo no válido o intento de Path Traversal.");
        }

        try {
            Files.write(destinationFile, fileBytes != null ? fileBytes : new byte[0]);
        } catch (IOException e) {
            throw new IllegalStateException("Fallo crítico al escribir el archivo de resolución en el disco.", e);
        }

        String extension = "pdf";
        int dotIndex = cleanedFileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = cleanedFileName.substring(dotIndex + 1).toLowerCase();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        long userId = 1L; // Fallback to 1 if no authentication context
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            userId = userDetails.getId();
        }

        String sql = "INSERT INTO documentos (nombre_original, ruta_almacenamiento, " +
                     "tipo_extension, tamano_bytes, id_usuario_subio) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        final long finalUserId = userId;
        final String finalExtension = extension;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cleanedFileName);
            ps.setString(2, destinationFile.toString());
            ps.setString(3, finalExtension);
            ps.setLong(4, fileBytes != null ? fileBytes.length : 0);
            ps.setLong(5, finalUserId);
            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id_documento")) {
            return ((Number) keyHolder.getKeys().get("id_documento")).longValue();
        }
        throw new IllegalStateException(
                messageSource.getMessage("resolution.error.document-save-failed", null, LocaleContextHolder.getLocale()));
    }
}
