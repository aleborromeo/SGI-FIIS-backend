package com.sgi.fiis.resoluciones.domain.port.out;

public interface TramiteRepositoryPort {
    boolean existeTramite(Long idTramite);
    void actualizarEstadoAAprobadoConResolucion(Long idTramite);
}
