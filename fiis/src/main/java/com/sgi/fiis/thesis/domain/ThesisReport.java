package com.sgi.fiis.thesis.domain;

import java.time.LocalDateTime;
import com.sgi.fiis.thesis.domain.exception.InvalidStateTransitionException;

public class ThesisReport {
    private Integer idInformeTesis;
    private Integer idPlanTesis;
    private String tituloFinal;
    private Integer idDocumentoTesis;
    private LocalDateTime fechaPresentacion;
    private ThesisReportStatus estadoInforme;

    public ThesisReport(Integer idInformeTesis, Integer idPlanTesis, String tituloFinal,
                        Integer idDocumentoTesis, LocalDateTime fechaPresentacion,
                        ThesisReportStatus estadoInforme) {
        this.idInformeTesis = idInformeTesis;
        this.idPlanTesis = idPlanTesis;
        this.tituloFinal = tituloFinal;
        this.idDocumentoTesis = idDocumentoTesis;
        this.fechaPresentacion = fechaPresentacion;
        this.estadoInforme = estadoInforme == null ? ThesisReportStatus.EN_REVISION : estadoInforme;
    }

    public static ThesisReport nuevo(Integer idPlanTesis, String tituloFinal, Integer idDocumentoTesis) {
        return new ThesisReport(null, idPlanTesis, tituloFinal, idDocumentoTesis, null, ThesisReportStatus.EN_REVISION);
    }

    public void aprobar() {
        estadoInforme = ThesisReportStatus.APROBADO;
    }

    public void observar() {
        if (estadoInforme == ThesisReportStatus.APROBADO) {
            throw new InvalidStateTransitionException("No se puede observar un informe aprobado");
        }
        estadoInforme = ThesisReportStatus.OBSERVADO;
    }

    public Integer getIdInformeTesis() { return idInformeTesis; }
    public Integer getIdPlanTesis() { return idPlanTesis; }
    public String getTituloFinal() { return tituloFinal; }
    public Integer getIdDocumentoTesis() { return idDocumentoTesis; }
    public LocalDateTime getFechaPresentacion() { return fechaPresentacion; }
    public ThesisReportStatus getEstadoInforme() { return estadoInforme; }
}
