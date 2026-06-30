package pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoPlanTesis;
import pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.entity.PlanTesisEntity;

public interface PlanTesisJpaRepository extends JpaRepository<PlanTesisEntity, Integer> {
    List<PlanTesisEntity> findByIdEstudiante(Long idEstudiante);
    List<PlanTesisEntity> findByIdGrupo(Integer idGrupo);
    List<PlanTesisEntity> findByEstadoPlan(EstadoPlanTesis estadoPlan);
}
