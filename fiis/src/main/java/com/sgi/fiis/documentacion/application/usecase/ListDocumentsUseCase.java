package com.sgi.fiis.documentacion.application.usecase;

import com.sgi.fiis.documentacion.application.dto.DocumentResponseDto;
import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;

import java.util.List;

public class ListDocumentsUseCase {

    private final DocumentRepositoryPort documentRepositoryPort;

    public ListDocumentsUseCase(DocumentRepositoryPort documentRepositoryPort) {
        this.documentRepositoryPort = documentRepositoryPort;
    }

    @SuppressWarnings("all")
    public List<DocumentResponseDto> execute() {
        return documentRepositoryPort.findAll().stream()
                .filter(doc -> doc.isActive())
                .map(this::toDto)
                .toList();
    }

    private DocumentResponseDto toDto(Document doc) {
        return new DocumentResponseDto(
                doc.getId(),
                doc.getOriginalName(),
                doc.getExtension(),
                doc.getSizeBytes(),
                doc.getUploadedById(),
                doc.getUploadDate()
        );
    }
}
