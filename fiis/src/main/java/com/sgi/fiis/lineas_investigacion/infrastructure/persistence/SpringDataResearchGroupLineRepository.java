package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;


public interface SpringDataResearchGroupLineRepository extends JpaRepository<ResearchGroupLineEntity, ResearchGroupLineId> {
    boolean existsByGroupIdAndLineId(Integer groupId, Integer lineId);
}
