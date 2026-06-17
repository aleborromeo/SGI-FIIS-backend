package com.sgi.fiis.shared.domain.exception;

public class BusinessRuleValidationException extends RuntimeException {
    public BusinessRuleValidationException(String message) {
        super(message);
    }
}
