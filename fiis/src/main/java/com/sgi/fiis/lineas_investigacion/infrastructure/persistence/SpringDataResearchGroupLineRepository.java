package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataResearchGroupLineRepository extends JpaRepository<ResearchGroupLineEntity, ResearchGroupLineId> {
    boolean existsByGroupIdAndLineId(Integer groupId, Integer lineId);
}
