package com.sgi.fiis.observations.domain.exception;

public class InvalidRemedyException extends RuntimeException {
    public InvalidRemedyException(Integer observationId) {
        super("No se puede subsanar la observación con ID: " + observationId
                + ". Solo se pueden subsanar observaciones en estado PENDIENTE.");
    }
}
