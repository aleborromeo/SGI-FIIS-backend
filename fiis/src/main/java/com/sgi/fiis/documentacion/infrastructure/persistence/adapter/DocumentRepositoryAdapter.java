package com.sgi.fiis.documentacion.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;
import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.infrastructure.persistence.entity.DocumentEntity;
import com.sgi.fiis.documentacion.infrastructure.persistence.repository.JpaDocumentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<Document> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
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
                .build();
    }
}