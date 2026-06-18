package com.sgi.fiis.observations.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * JPA Entity for 'subsanaciones' table.
 */
@Entity
@Table(name = "subsanaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemedyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subsanacion")
    private Integer id;

    @Column(name = "id_observacion", nullable = false)
    private Integer observationId;

    @Column(name = "id_solicitante", nullable = false)
    private Integer applicantId;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "id_documento_adjunto")
    private Integer attachedDocumentId;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime updatedAt;
}
