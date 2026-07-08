package com.sgi.fiis.thesis.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import com.sgi.fiis.thesis.domain.ThesisPlan;
import com.sgi.fiis.thesis.infrastructure.persistence.entity.ThesisPlanEntity;

@Component
public class ThesisPlanMapper {
    public ThesisPlan toDomain(ThesisPlanEntity e) {
        return new ThesisPlan(e.getIdPlanTesis(), e.getTituloTesis(), e.getResumen(), e.getIdEstudiante(),
                e.getIdLinea(), e.getIdGrupo(), e.getIdDocumentoActual(), e.getEstadoPlan(),
                e.getFechaCreacion(), e.getFechaActualizacion());
    }
    public ThesisPlanEntity toEntity(ThesisPlan d) {
        ThesisPlanEntity e = new ThesisPlanEntity();
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
