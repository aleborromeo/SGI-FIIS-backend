package com.sgi.fiis.thesis.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sgi.fiis.thesis.domain.ThesisPlanStatus;
import com.sgi.fiis.thesis.infrastructure.persistence.entity.ThesisPlanEntity;

public interface ThesisPlanJpaRepository extends JpaRepository<ThesisPlanEntity, Integer> {
    List<ThesisPlanEntity> findByIdEstudiante(Long idEstudiante);
    List<ThesisPlanEntity> findByIdGrupo(Integer idGrupo);
    List<ThesisPlanEntity> findByEstadoPlan(ThesisPlanStatus estadoPlan);
}
