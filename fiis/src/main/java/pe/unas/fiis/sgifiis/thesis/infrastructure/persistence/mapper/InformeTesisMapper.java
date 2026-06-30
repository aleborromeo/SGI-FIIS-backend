package pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import pe.unas.fiis.sgifiis.thesis.domain.InformeTesis;
import pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.entity.InformeTesisEntity;

@Component
public class InformeTesisMapper {
    public InformeTesis toDomain(InformeTesisEntity e) {
        return new InformeTesis(e.getIdInformeTesis(), e.getIdPlanTesis(), e.getTituloFinal(),
                e.getIdDocumentoTesis(), e.getFechaPresentacion(), e.getEstadoInforme());
    }
    public InformeTesisEntity toEntity(InformeTesis d) {
        InformeTesisEntity e = new InformeTesisEntity();
        e.setIdInformeTesis(d.getIdInformeTesis());
        e.setIdPlanTesis(d.getIdPlanTesis());
        e.setTituloFinal(d.getTituloFinal());
        e.setIdDocumentoTesis(d.getIdDocumentoTesis());
        e.setFechaPresentacion(d.getFechaPresentacion());
        e.setEstadoInforme(d.getEstadoInforme());
        return e;
    }
}
