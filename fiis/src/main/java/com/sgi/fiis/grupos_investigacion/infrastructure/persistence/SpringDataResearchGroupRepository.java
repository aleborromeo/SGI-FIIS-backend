package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataResearchGroupRepository extends JpaRepository<ResearchGroupEntity, Integer> {
    boolean existsByGroupCode(String groupCode);
}
