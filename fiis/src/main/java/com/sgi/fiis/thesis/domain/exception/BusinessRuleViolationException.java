package com.sgi.fiis.thesis.domain.exception;

import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;

public class BusinessRuleViolationException extends BusinessRuleValidationException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }

    public BusinessRuleViolationException(String errorKey, Object... args) {
        super(errorKey, args);
    }
}
