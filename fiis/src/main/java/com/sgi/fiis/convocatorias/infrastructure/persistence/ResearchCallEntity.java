package com.sgi.fiis.convocatorias.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convocatorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResearchCallEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_convocatoria")
    private Integer id;

    @Column(name = "titulo_convocatoria", nullable = false, length = 150)
    private String title;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate startDate;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate endDate;

    @Column(name = "estado", nullable = false, length = 20)
    private String status;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(ZoneId.systemDefault());
        if (status == null) {
            status = "ABIERTA";
        }
    }
}
