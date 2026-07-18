package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchGroupJpaRepository extends JpaRepository<ResearchGroupEntity, Integer> {

    ResearchGroupEntity findByCurrentCoordinatorId(Long coordinatorId);
}
