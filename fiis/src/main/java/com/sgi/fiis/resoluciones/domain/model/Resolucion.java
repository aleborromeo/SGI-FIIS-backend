package com.sgi.fiis.resoluciones.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de dominio para una Resolución.
 * Usa record de Java para evitar duplicidad de código con la capa de infraestructura.
 */
public record Resolucion(
        Long idResolucion,
        String numeroResolucion,
        LocalDate fechaEmision,
        String asunto,
        Long idTramite,
        Long idDocumentoAdjunto,
        LocalDateTime fechaRegistro
) {}
