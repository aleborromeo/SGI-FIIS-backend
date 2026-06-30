package com.sgi.fiis.observations.domain.exception;

public class ObservationNotFoundException extends RuntimeException {
    public ObservationNotFoundException(Integer observationId) {
        super("Observación no encontrada con ID: " + observationId);
    }
}
