package com.sgi.fiis.observaciones.domain.exception;

public class SubsanacionInvalidaException extends RuntimeException {
    public SubsanacionInvalidaException(Integer idObservacion) {
        super("No se puede subsanar la observación con ID: " + idObservacion
                + ". Solo se pueden subsanar observaciones en estado PENDIENTE.");
    }
}
