package com.sgi.fiis.thesis.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import com.sgi.fiis.thesis.domain.ThesisReportStatus;

@Entity
@Table(name = "informes_tesis")
public class ThesisReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_informe_tesis")
    private Integer idInformeTesis;

    @Column(name = "id_plan_tesis", nullable = false)
    private Integer idPlanTesis;

    @Column(name = "titulo_final", nullable = false, length = 500)
    private String tituloFinal;

    @Column(name = "id_documento_tesis", nullable = false)
    private Integer idDocumentoTesis;

    @CreationTimestamp
    @Column(name = "fecha_presentacion", nullable = false, updatable = false)
    private LocalDateTime fechaPresentacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_informe", nullable = false, length = 30)
    private ThesisReportStatus estadoInforme = ThesisReportStatus.EN_REVISION;

    public Integer getIdInformeTesis() { return idInformeTesis; }
    public void setIdInformeTesis(Integer idInformeTesis) { this.idInformeTesis = idInformeTesis; }
    public Integer getIdPlanTesis() { return idPlanTesis; }
    public void setIdPlanTesis(Integer idPlanTesis) { this.idPlanTesis = idPlanTesis; }
    public String getTituloFinal() { return tituloFinal; }
    public void setTituloFinal(String tituloFinal) { this.tituloFinal = tituloFinal; }
    public Integer getIdDocumentoTesis() { return idDocumentoTesis; }
    public void setIdDocumentoTesis(Integer idDocumentoTesis) { this.idDocumentoTesis = idDocumentoTesis; }
    public LocalDateTime getFechaPresentacion() { return fechaPresentacion; }
    public void setFechaPresentacion(LocalDateTime fechaPresentacion) { this.fechaPresentacion = fechaPresentacion; }
    public ThesisReportStatus getEstadoInforme() { return estadoInforme; }
    public void setEstadoInforme(ThesisReportStatus estadoInforme) { this.estadoInforme = estadoInforme; }
}
