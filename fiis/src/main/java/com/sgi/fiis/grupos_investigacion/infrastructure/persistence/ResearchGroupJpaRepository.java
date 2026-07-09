package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResearchGroupJpaRepository extends JpaRepository<ResearchGroupEntity, Integer> {
}
