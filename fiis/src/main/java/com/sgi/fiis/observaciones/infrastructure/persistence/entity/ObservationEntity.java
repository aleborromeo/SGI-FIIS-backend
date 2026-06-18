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
 * JPA entity for the 'observaciones' table.
 */
@Entity
@Table(name = "observaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_observacion")
    private Integer id;

    @Column(name = "id_tramite", nullable = false)
    private Integer procedureId;

    @Column(name = "id_revisor", nullable = false)
    private Integer reviewerId;

    @Column(name = "tipo_observacion", nullable = false, length = 30)
    private String observationType;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "estado_observacion", nullable = false, length = 20)
    private String status;

    @Column(name = "rol_revisor", nullable = false, length = 50)
    private String reviewerRole;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime updatedAt;
}
