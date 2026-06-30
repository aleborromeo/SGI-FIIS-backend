package pe.unas.fiis.sgifiis.thesis.domain.port.out;

import java.util.List;
import java.util.Optional;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoPlanTesis;
import pe.unas.fiis.sgifiis.thesis.domain.PlanTesis;

public interface PlanTesisRepositoryPort {
    PlanTesis save(PlanTesis planTesis);
    Optional<PlanTesis> findById(Integer idPlanTesis);
    List<PlanTesis> findByEstudiante(Long idEstudiante);
    List<PlanTesis> findByGrupo(Integer idGrupo);
    List<PlanTesis> findByEstado(EstadoPlanTesis estadoPlan);
}
