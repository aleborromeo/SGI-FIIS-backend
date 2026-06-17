package com.sgi.fiis.documentacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sgi.fiis.documentacion.infrastructure.persistence.entity.DocumentEntity;

@Repository
public interface JpaDocumentRepository extends JpaRepository<DocumentEntity, Long> {
}