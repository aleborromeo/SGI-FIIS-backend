package com.sgi.fiis.convocatorias.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ResearchCallJpaRepository extends JpaRepository<ResearchCallEntity, Integer> {
    List<ResearchCallEntity> findByStatus(String status);

    List<ResearchCallEntity> findByStatusAndEndDateGreaterThanEqual(String status, LocalDate today);

    @Modifying
    @Transactional
    @Query("UPDATE ResearchCallEntity e SET e.status = 'CERRADA', e.updatedAt = CURRENT_TIMESTAMP WHERE e.status = 'ABIERTA' AND e.endDate < :today")
    int closeExpiredCalls(@Param("today") LocalDate today);
}
