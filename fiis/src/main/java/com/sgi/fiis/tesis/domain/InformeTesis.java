package pe.unas.fiis.sgifiis.thesis.domain;

import java.time.LocalDateTime;
import pe.unas.fiis.sgifiis.thesis.domain.exception.TransicionEstadoInvalidaException;

public class InformeTesis {
    private Integer idInformeTesis;
    private Integer idPlanTesis;
    private String tituloFinal;
    private Integer idDocumentoTesis;
    private LocalDateTime fechaPresentacion;
    private EstadoInformeTesis estadoInforme;

    public InformeTesis(Integer idInformeTesis, Integer idPlanTesis, String tituloFinal,
                        Integer idDocumentoTesis, LocalDateTime fechaPresentacion,
                        EstadoInformeTesis estadoInforme) {
        this.idInformeTesis = idInformeTesis;
        this.idPlanTesis = idPlanTesis;
        this.tituloFinal = tituloFinal;
        this.idDocumentoTesis = idDocumentoTesis;
        this.fechaPresentacion = fechaPresentacion;
        this.estadoInforme = estadoInforme == null ? EstadoInformeTesis.EN_REVISION : estadoInforme;
    }

    public static InformeTesis nuevo(Integer idPlanTesis, String tituloFinal, Integer idDocumentoTesis) {
        return new InformeTesis(null, idPlanTesis, tituloFinal, idDocumentoTesis, null, EstadoInformeTesis.EN_REVISION);
    }

    public void aprobar() {
        estadoInforme = EstadoInformeTesis.APROBADO;
    }

    public void observar() {
        if (estadoInforme == EstadoInformeTesis.APROBADO) {
            throw new TransicionEstadoInvalidaException("No se puede observar un informe aprobado");
        }
        estadoInforme = EstadoInformeTesis.OBSERVADO;
    }

    public Integer getIdInformeTesis() { return idInformeTesis; }
    public Integer getIdPlanTesis() { return idPlanTesis; }
    public String getTituloFinal() { return tituloFinal; }
    public Integer getIdDocumentoTesis() { return idDocumentoTesis; }
    public LocalDateTime getFechaPresentacion() { return fechaPresentacion; }
    public EstadoInformeTesis getEstadoInforme() { return estadoInforme; }
}
