package com.sgi.fiis.observaciones.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla 'subsanaciones'.
 */
@Entity
@Table(name = "subsanaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubsanacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subsanacion")
    private Integer idSubsanacion;

    @Column(name = "id_observacion", nullable = false)
    private Integer idObservacion;

    @Column(name = "id_solicitante", nullable = false)
    private Integer idSolicitante;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "id_documento_adjunto")
    private Integer idDocumentoAdjunto;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
