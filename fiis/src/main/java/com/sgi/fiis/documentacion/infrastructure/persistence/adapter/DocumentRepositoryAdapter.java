package com.sgi.fiis.documentacion.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;
import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.infrastructure.persistence.entity.DocumentEntity;
import com.sgi.fiis.documentacion.infrastructure.persistence.repository.JpaDocumentRepository;

import java.util.Optional;

@Component
public class DocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final JpaDocumentRepository repository;

    public DocumentRepositoryAdapter(JpaDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Document save(Document domain) {
        DocumentEntity entity = toEntity(domain);
        DocumentEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Document> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    private DocumentEntity toEntity(Document domain) {
        DocumentEntity entity = new DocumentEntity();
        entity.setId(domain.getId());
        entity.setOriginalName(domain.getOriginalName());
        entity.setStoragePath(domain.getStoragePath());
        entity.setExtension(domain.getExtension());
        entity.setSizeBytes(domain.getSizeBytes());
        entity.setUploadedById(domain.getUploadedById());
        entity.setUploadDate(domain.getUploadDate());
        entity.setActive(domain.isActive());
        entity.setProyectoId(domain.getProyectoId());
        entity.setTramiteId(domain.getTramiteId());
        entity.setPlanTesisId(domain.getPlanTesisId());
        entity.setInformeId(domain.getInformeId());
        entity.setEsSubsanacion(domain.isEsSubsanacion());
        return entity;
    }

    private Document toDomain(DocumentEntity entity) {
        return Document.builder()
                .id(entity.getId())
                .originalName(entity.getOriginalName())
                .storagePath(entity.getStoragePath())
                .extension(entity.getExtension())
                .sizeBytes(entity.getSizeBytes())
                .uploadedById(entity.getUploadedById())
                .uploadDate(entity.getUploadDate())
                .active(entity.isActive())
                .proyectoId(entity.getProyectoId())
                .tramiteId(entity.getTramiteId())
                .planTesisId(entity.getPlanTesisId())
                .informeId(entity.getInformeId())
                .esSubsanacion(entity.isEsSubsanacion())
                .build();
    }
}