package com.sgi.fiis.resolutions.domain.port.in;

import java.time.LocalDate;

public record IssueResolutionCommand(
    String numeroResolucion,
    LocalDate fechaEmision,
    String asunto,
    Long idTramite,
    byte[] fileBytes,
    String fileName,
    String contentType
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueResolutionCommand that = (IssueResolutionCommand) o;
        return java.util.Objects.equals(numeroResolucion, that.numeroResolucion) &&
               java.util.Objects.equals(fechaEmision, that.fechaEmision) &&
               java.util.Objects.equals(asunto, that.asunto) &&
               java.util.Objects.equals(idTramite, that.idTramite) &&
               java.util.Arrays.equals(fileBytes, that.fileBytes) &&
               java.util.Objects.equals(fileName, that.fileName) &&
               java.util.Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        int result = java.util.Objects.hash(numeroResolucion, fechaEmision, asunto, idTramite, fileName, contentType);
        result = 31 * result + java.util.Arrays.hashCode(fileBytes);
        return result;
    }

    @Override
    public String toString() {
        return "IssueResolutionCommand{" +
               "numeroResolucion='" + numeroResolucion + '\'' +
               ", fechaEmision=" + fechaEmision +
               ", asunto='" + asunto + '\'' +
               ", idTramite=" + idTramite +
               ", fileBytes=" + (fileBytes != null ? "[array of size " + fileBytes.length + "]" : "null") +
               ", fileName='" + fileName + '\'' +
               ", contentType='" + contentType + '\'' +
               '}';
    }
}
