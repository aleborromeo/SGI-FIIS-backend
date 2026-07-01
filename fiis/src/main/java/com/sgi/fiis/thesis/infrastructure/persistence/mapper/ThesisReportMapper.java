package com.sgi.fiis.thesis.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import com.sgi.fiis.thesis.domain.ThesisReport;
import com.sgi.fiis.thesis.infrastructure.persistence.entity.ThesisReportEntity;

@Component
public class ThesisReportMapper {
    public ThesisReport toDomain(ThesisReportEntity e) {
        return new ThesisReport(e.getIdInformeTesis(), e.getIdPlanTesis(), e.getTituloFinal(),
                e.getIdDocumentoTesis(), e.getFechaPresentacion(), e.getEstadoInforme());
    }
    public ThesisReportEntity toEntity(ThesisReport d) {
        ThesisReportEntity e = new ThesisReportEntity();
        e.setIdInformeTesis(d.getIdInformeTesis());
        e.setIdPlanTesis(d.getIdPlanTesis());
        e.setTituloFinal(d.getTituloFinal());
        e.setIdDocumentoTesis(d.getIdDocumentoTesis());
        e.setFechaPresentacion(d.getFechaPresentacion());
        e.setEstadoInforme(d.getEstadoInforme());
        return e;
    }
}
