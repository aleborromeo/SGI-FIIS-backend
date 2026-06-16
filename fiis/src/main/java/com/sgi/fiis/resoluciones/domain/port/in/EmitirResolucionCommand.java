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
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmitirResolucionCommand that = (EmitirResolucionCommand) o;
        return java.util.Objects.equals(numeroResolucion, that.numeroResolucion) &&
               java.util.Objects.equals(fechaEmision, that.fechaEmision) &&
               java.util.Objects.equals(asunto, that.asunto) &&
               java.util.Objects.equals(idTramite, that.idTramite) &&
               java.util.Arrays.equals(archivoBytes, that.archivoBytes) &&
               java.util.Objects.equals(nombreArchivo, that.nombreArchivo) &&
               java.util.Objects.equals(tipoContenido, that.tipoContenido);
    }

    @Override
    public int hashCode() {
        int result = java.util.Objects.hash(numeroResolucion, fechaEmision, asunto, idTramite, nombreArchivo, tipoContenido);
        result = 31 * result + java.util.Arrays.hashCode(archivoBytes);
        return result;
    }

    @Override
    public String toString() {
        return "EmitirResolucionCommand{" +
               "numeroResolucion='" + numeroResolucion + '\'' +
               ", fechaEmision=" + fechaEmision +
               ", asunto='" + asunto + '\'' +
               ", idTramite=" + idTramite +
               ", archivoBytes=" + (archivoBytes != null ? "[array of size " + archivoBytes.length + "]" : "null") +
               ", nombreArchivo='" + nombreArchivo + '\'' +
               ", tipoContenido='" + tipoContenido + '\'' +
               '}';
    }
}
