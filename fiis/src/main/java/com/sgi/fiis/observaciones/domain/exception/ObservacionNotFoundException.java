package com.sgi.fiis.observaciones.domain.exception;

public class ObservacionNotFoundException extends RuntimeException {
    public ObservacionNotFoundException(Integer idObservacion) {
        super("Observación no encontrada con ID: " + idObservacion);
    }
}
