package com.sgi.fiis.documentacion.application.dto;

import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.domain.port.FileStoragePort;

import java.io.InputStream;

public class DownloadDocumentUseCase {
    private final DocumentRepositoryPort documentRepositoryPort;
    private final FileStoragePort fileStoragePort;

    public DownloadDocumentUseCase(DocumentRepositoryPort documentRepositoryPort, FileStoragePort fileStoragePort) {
        this.documentRepositoryPort = documentRepositoryPort;
        this.fileStoragePort = fileStoragePort;
    }

    public InputStream execute(Long documentId, Integer currentUserId, String currentUserRol) {
        // 1. Recuperar metadatos del documento
        Document document = documentRepositoryPort.findById(documentId)
                .orElseThrow(() -> new RuntimeException("El documento no existe."));

        if (!document.isActive()) {
            throw new RuntimeException("El documento solicitado no está disponible.");
        }

        // 2. Control de Acceso basado en Permisos de datos (RF-68, RNF-03, RNF-08)
        // Regla: El dueño o usuarios con roles de jerarquía (DIRECTOR_INVESTIGACION, DECANO, ADMIN) pueden descargar
        boolean isOwner = document.getUploadedById().equals(currentUserId);
        boolean isAuthority = currentUserRol.equals("DIRECTOR_INVESTIGACION") || 
                              currentUserRol.equals("DECANO") || 
                              currentUserRol.equals("ADMIN") ||
                              currentUserRol.equals("COORDINADOR_GRUPO");

        if (!isOwner && !isAuthority) {
            throw new RuntimeException("Acceso denegado: No cuenta con permisos para descargar este archivo.");
        }

        // 3. Cargar el flujo de bytes desde el puerto de almacenamiento físico
        return fileStoragePort.load(document.getStoragePath());
    }
}