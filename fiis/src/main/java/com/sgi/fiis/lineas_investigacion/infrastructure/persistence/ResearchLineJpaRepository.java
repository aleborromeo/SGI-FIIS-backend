package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResearchLineJpaRepository extends JpaRepository<ResearchLineEntity, Integer> {
}
