package com.sgi.fiis.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, Integer> {
}
