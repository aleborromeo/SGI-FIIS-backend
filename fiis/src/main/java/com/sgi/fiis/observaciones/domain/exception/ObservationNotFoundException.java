package com.sgi.fiis.observaciones.domain.exception;

public class ObservationNotFoundException extends RuntimeException {
    public ObservationNotFoundException(Integer observationId) {
        super("Observation not found with ID: " + observationId);
    }
}
