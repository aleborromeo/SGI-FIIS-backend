package com.sgi.fiis.resoluciones.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resoluciones")
@Getter
@Setter
@NoArgsConstructor
public class ResolucionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resolucion")
    private Long idResolucion;

    @Column(name = "numero_resolucion", nullable = false, unique = true, length = 100)
    private String numeroResolucion;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "asunto", nullable = false, length = 500)
    private String asunto;

    @Column(name = "id_tramite", nullable = false)
    private Long idTramite;

    @Column(name = "id_documento_adjunto", nullable = false)
    private Long idDocumentoAdjunto;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now(java.time.ZoneId.systemDefault());
        }
    }
}
