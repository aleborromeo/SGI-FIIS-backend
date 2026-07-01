package com.sgi.fiis.thesis.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sgi.fiis.thesis.infrastructure.persistence.entity.ThesisReportEntity;

public interface ThesisReportJpaRepository extends JpaRepository<ThesisReportEntity, Integer> {
    List<ThesisReportEntity> findByIdPlanTesis(Integer idPlanTesis);
}
