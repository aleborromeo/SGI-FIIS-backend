package com.sgi.fiis.documentacion.application.usecase;

import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.domain.port.FileStoragePort;
import com.sgi.fiis.documentacion.application.exception.DocumentNotFoundException;
import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;

import java.io.InputStream;

public class DownloadDocumentUseCase {
    private final DocumentRepositoryPort documentRepositoryPort;
    private final FileStoragePort fileStoragePort;

    public DownloadDocumentUseCase(DocumentRepositoryPort documentRepositoryPort, FileStoragePort fileStoragePort) {
        this.documentRepositoryPort = documentRepositoryPort;
        this.fileStoragePort = fileStoragePort;
    }

    public InputStream execute(Long documentId, Integer currentUserId, String currentUserRol) {
        Document document = documentRepositoryPort.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("El documento no existe."));

        if (!document.isActive()) {
            throw new DocumentAccessDeniedException("El documento solicitado no está disponible.");
        }

        // Control de acceso riguroso basado en roles e identidad (RNF-03, RNF-08)
        boolean isOwner = document.getUploadedById().equals(currentUserId);
        boolean isAuthority = currentUserRol.equals("DIRECTOR_INVESTIGACION") || 
                              currentUserRol.equals("DECANO") || 
                              currentUserRol.equals("ADMIN") ||
                              currentUserRol.equals("COORDINADOR_GRUPO");

        if (!isOwner && !isAuthority) {
            throw new DocumentAccessDeniedException("Acceso denegado: No posee permisos sobre este archivo.");
        }

        return fileStoragePort.load(document.getStoragePath());
    }
}