package com.sgi.fiis.documentacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sgi.fiis.documentacion.infrastructure.persistence.entity.DocumentEntity;

public interface JpaDocumentRepository extends JpaRepository<DocumentEntity, Long> {
}