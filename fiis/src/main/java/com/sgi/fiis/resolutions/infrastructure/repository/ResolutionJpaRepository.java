package com.sgi.fiis.resolutions.infrastructure.repository;

import com.sgi.fiis.resolutions.infrastructure.entity.ResolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResolutionJpaRepository extends JpaRepository<ResolutionEntity, Long> {
    boolean existsByNumeroResolucion(String numeroResolucion);
}
