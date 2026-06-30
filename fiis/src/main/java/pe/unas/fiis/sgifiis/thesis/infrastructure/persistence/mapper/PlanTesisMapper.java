package pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import pe.unas.fiis.sgifiis.thesis.domain.PlanTesis;
import pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.entity.PlanTesisEntity;

@Component
public class PlanTesisMapper {
    public PlanTesis toDomain(PlanTesisEntity e) {
        return new PlanTesis(e.getIdPlanTesis(), e.getTituloTesis(), e.getResumen(), e.getIdEstudiante(),
                e.getIdLinea(), e.getIdGrupo(), e.getIdDocumentoActual(), e.getEstadoPlan(),
                e.getFechaCreacion(), e.getFechaActualizacion());
    }
    public PlanTesisEntity toEntity(PlanTesis d) {
        PlanTesisEntity e = new PlanTesisEntity();
        e.setIdPlanTesis(d.getIdPlanTesis());
        e.setTituloTesis(d.getTituloTesis());
        e.setResumen(d.getResumen());
        e.setIdEstudiante(d.getIdEstudiante());
        e.setIdLinea(d.getIdLinea());
        e.setIdGrupo(d.getIdGrupo());
        e.setIdDocumentoActual(d.getIdDocumentoActual());
        e.setEstadoPlan(d.getEstadoPlan());
        e.setFechaCreacion(d.getFechaCreacion());
        e.setFechaActualizacion(d.getFechaActualizacion());
        return e;
    }
}
