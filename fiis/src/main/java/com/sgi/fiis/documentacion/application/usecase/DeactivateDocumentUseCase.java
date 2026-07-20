package com.sgi.fiis.documentacion.application.usecase;

import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;
import com.sgi.fiis.documentacion.application.exception.DocumentNotFoundException;
import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;
import org.springframework.stereotype.Service;

@Service
public class DeactivateDocumentUseCase {

    private final DocumentRepositoryPort documentRepositoryPort;

    public DeactivateDocumentUseCase(DocumentRepositoryPort documentRepositoryPort) {
        this.documentRepositoryPort = documentRepositoryPort;
    }

    @Auditable(action = "DEACTIVATE_DOCUMENT", table = "documentos")
    public void execute(Long documentId, Long currentUserId, String currentUserRol) {

        // 1. Buscar el documento en la BD
        Document document = documentRepositoryPort.findById(documentId)
                .orElseThrow(() ->
                        new DocumentNotFoundException("El documento no existe.")
                );

        // 2. Control de seguridad
        boolean isOwner = document.getUploadedById().equals(currentUserId);
        boolean isAdmin = currentUserRol.equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new DocumentAccessDeniedException(
                    "No tiene permisos para deshabilitar este documento."
            );
        }

        // 3. Borrado lógico
        document.deactivate();

        // 4. Persistir cambios
        documentRepositoryPort.save(document);
    }
}