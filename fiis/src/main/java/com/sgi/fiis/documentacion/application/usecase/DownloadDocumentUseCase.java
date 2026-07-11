package com.sgi.fiis.documentacion.application.usecase;

import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.domain.port.FileStoragePort;
import com.sgi.fiis.documentacion.application.exception.DocumentNotFoundException;
import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;
import com.sgi.fiis.resolutions.domain.port.out.ResolutionRepositoryPort;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;

import java.io.InputStream;

public class DownloadDocumentUseCase {
    private final DocumentRepositoryPort documentRepositoryPort;
    private final FileStoragePort fileStoragePort;
    private final ResolutionRepositoryPort resolutionRepositoryPort;
    private final ProcedureRepositoryPort procedureRepositoryPort;

    public DownloadDocumentUseCase(DocumentRepositoryPort documentRepositoryPort,
                                   FileStoragePort fileStoragePort,
                                   ResolutionRepositoryPort resolutionRepositoryPort,
                                   ProcedureRepositoryPort procedureRepositoryPort) {
        this.documentRepositoryPort = documentRepositoryPort;
        this.fileStoragePort = fileStoragePort;
        this.resolutionRepositoryPort = resolutionRepositoryPort;
        this.procedureRepositoryPort = procedureRepositoryPort;
    }

    public DocumentDownloadResult execute(Long documentId, Long currentUserId, String currentUserRol) {
        Document document = documentRepositoryPort.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("El documento no existe."));

        if (!document.isActive()) {
            throw new DocumentAccessDeniedException("El documento solicitado no está disponible.");
        }

        boolean isOwner = document.getUploadedById() != null && document.getUploadedById().equals(currentUserId);
        boolean isAuthority = currentUserRol != null && (
                              currentUserRol.equals("DIRECTOR_INVESTIGACION") || 
                              currentUserRol.equals("DECANO") || 
                              currentUserRol.equals("ADMIN") ||
                              currentUserRol.equals("COORDINADOR_GRUPO"));

        if (!isOwner && !isAuthority && !isProcedureApplicant(documentId, currentUserId)) {
            throw new DocumentAccessDeniedException("Acceso denegado: No posee permisos sobre este archivo.");
        }

        InputStream stream;
        try {
            stream = fileStoragePort.load(document.getStoragePath());
        } catch (Exception e) {
            throw new DocumentNotFoundException("No se pudo acceder al archivo del documento.");
        }
        return new DocumentDownloadResult(stream, document.getOriginalName());
    }

    private boolean isProcedureApplicant(Long documentId, Long currentUserId) {
        if (resolutionRepositoryPort == null || procedureRepositoryPort == null) {
            return false;
        }
        return resolutionRepositoryPort.findByDocumentAdjuntoId(documentId)
                .flatMap(resolution -> procedureRepositoryPort.findById(resolution.idTramite()))
                .map(procedure -> procedure.getApplicantId() != null && procedure.getApplicantId().equals(currentUserId))
                .orElse(false);
    }
}