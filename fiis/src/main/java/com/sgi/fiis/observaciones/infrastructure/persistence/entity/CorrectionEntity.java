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
 * JPA entity for the 'subsanaciones' table.
 */
@Entity
@Table(name = "subsanaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subsanacion")
    private Integer id;

    @Column(name = "id_observacion", nullable = false)
    private Integer observationId;

    @Column(name = "id_solicitante", nullable = false)
    private Integer requesterId;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "id_documento_adjunto")
    private Integer attachedDocumentId;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime updatedAt;
}
