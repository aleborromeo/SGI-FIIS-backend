package com.sgi.fiis.thesis.domain.port.out;

import java.util.List;
import java.util.Optional;
import com.sgi.fiis.thesis.domain.ThesisPlanStatus;
import com.sgi.fiis.thesis.domain.ThesisPlan;

public interface ThesisPlanRepositoryPort {
    ThesisPlan save(ThesisPlan thesisPlan);
    Optional<ThesisPlan> findById(Integer idPlanTesis);
    List<ThesisPlan> findByEstudiante(Long idEstudiante);
    List<ThesisPlan> findByGrupo(Integer idGrupo);
    List<ThesisPlan> findByEstado(ThesisPlanStatus estadoPlan);
}
