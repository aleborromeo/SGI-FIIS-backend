package com.sgi.fiis.resoluciones.infrastructure.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resoluciones")
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

    // Getters and Setters

    public Long getIdResolucion() {
        return idResolucion;
    }

    public void setIdResolucion(Long idResolucion) {
        this.idResolucion = idResolucion;
    }

    public String getNumeroResolucion() {
        return numeroResolucion;
    }

    public void setNumeroResolucion(String numeroResolucion) {
        this.numeroResolucion = numeroResolucion;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public Long getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(Long idTramite) {
        this.idTramite = idTramite;
    }

    public Long getIdDocumentoAdjunto() {
        return idDocumentoAdjunto;
    }

    public void setIdDocumentoAdjunto(Long idDocumentoAdjunto) {
        this.idDocumentoAdjunto = idDocumentoAdjunto;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
