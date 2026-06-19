package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lineas_investigacion")
@Getter
@Setter
public class ResearchLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_linea")
    private Integer id;

    @Column(name = "nombre_linea", nullable = false, unique = true, length = 150)
    private String lineName;

    @Column(name = "es_activa", nullable = false)
    private boolean active;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime updatedAt;
}
