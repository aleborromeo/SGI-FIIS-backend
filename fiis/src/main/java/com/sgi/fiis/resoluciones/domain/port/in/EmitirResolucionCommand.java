package com.sgi.fiis.resoluciones.domain.port.in;

import java.time.LocalDate;

public record EmitirResolucionCommand(
    String numeroResolucion,
    LocalDate fechaEmision,
    String asunto,
    Long idTramite,
    byte[] archivoBytes,
    String nombreArchivo,
    String tipoContenido
) {}
