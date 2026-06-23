package com.sgi.fiis.documentacion.application.usecase;

import com.sgi.fiis.documentacion.application.dto.DocumentResponseDto;
import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.domain.port.FileStoragePort;

import java.io.InputStream;
import java.time.LocalDateTime;

public class UploadDocumentUseCase {
    private final DocumentRepositoryPort documentRepositoryPort;
    private final FileStoragePort fileStoragePort;

    public UploadDocumentUseCase(DocumentRepositoryPort documentRepositoryPort, FileStoragePort fileStoragePort) {
        this.documentRepositoryPort = documentRepositoryPort;
        this.fileStoragePort = fileStoragePort;
    }

    public DocumentResponseDto execute(InputStream fileStream, String originalName, Long sizeBytes, Long userId) {
        // 1. Extraer la extensión del archivo
        String extension = "";
        int i = originalName.lastIndexOf('.');
        if (i > 0) {
            extension = originalName.substring(i + 1).toUpperCase();
        }

        // 2. Almacenar físicamente el binario en el servidor primero para obtener su ruta real (RF-66)
        String storagePath = fileStoragePort.store(fileStream, originalName);

        // 3. Crear la instancia usando el patrón Builder Estático
        Document document = Document.builder()
                .originalName(originalName)
                .storagePath(storagePath)
                .extension(extension)
                .sizeBytes(sizeBytes)
                .uploadedById(userId)
                .uploadDate(LocalDateTime.now(java.time.ZoneId.systemDefault()))
                .active(true)
                .build();

        // 4. Persistir los metadatos mapeados en la Base de Datos (RF-66)
        Document savedDocument = documentRepositoryPort.save(document);

        // 5. Retornar el DTO de respuesta estructurado sin tipos primitivos sueltos
        return new DocumentResponseDto(
            savedDocument.getId(),
            savedDocument.getOriginalName(),
            savedDocument.getExtension(),
            savedDocument.getSizeBytes(),
            savedDocument.getUploadedById(),
            savedDocument.getUploadDate()
        );
    }
}