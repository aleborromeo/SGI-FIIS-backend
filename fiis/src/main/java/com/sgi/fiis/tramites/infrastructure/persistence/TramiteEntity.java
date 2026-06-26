package com.sgi.fiis.tramites.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tramites")
@Getter
@Setter
public class TramiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite")
    private Long id;

    @Column(name = "codigo_tramite", nullable = false, unique = true, length = 30)
    private String codigoTramite;

    @Column(name = "tipo_tramite", nullable = false, length = 50)
    private String tipoTramite;

    @Column(name = "id_solicitante", nullable = false)
    private Long idSolicitante;

    @Column(name = "id_grupo")
    private Long idGrupo;

    @Column(name = "estado_actual", nullable = false, length = 50)
    private String estadoActual;

    // Nullable: null cuando el trámite está RECHAZADO o en OBSERVADO por Coordinador
    @Column(name = "rol_revisor_actual", length = 50)
    private String rolRevisorActual;

    // Columna añadida en V9 — refleja la observación vigente del trámite
    @Column(name = "observacion_actual")
    private String observacionActual;

    @Column(name = "id_referencia_proyecto")
    private Long idReferenciaProyecto;

    @Column(name = "id_referencia_tesis")
    private Long idReferenciaTesis;

    @Column(name = "id_referencia_informe")
    private Long idReferenciaInforme;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
