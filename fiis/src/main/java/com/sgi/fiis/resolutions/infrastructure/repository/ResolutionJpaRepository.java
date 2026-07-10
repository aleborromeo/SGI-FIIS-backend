package com.sgi.fiis.resolutions.infrastructure.repository;

import com.sgi.fiis.resolutions.infrastructure.entity.ResolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResolutionJpaRepository extends JpaRepository<ResolutionEntity, Long> {
    boolean existsByNumeroResolucion(String numeroResolucion);
}
