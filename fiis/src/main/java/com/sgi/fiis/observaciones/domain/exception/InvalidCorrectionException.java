package com.sgi.fiis.observaciones.domain.exception;

public class InvalidCorrectionException extends RuntimeException {
    public InvalidCorrectionException(Integer observationId) {
        super("Cannot correct observation with ID: " + observationId
                + ". Only observations with PENDING status can be corrected.");
    }
}
