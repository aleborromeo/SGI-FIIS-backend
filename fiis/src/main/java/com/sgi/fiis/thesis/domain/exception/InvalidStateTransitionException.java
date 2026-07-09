package com.sgi.fiis.thesis.domain.exception;

import com.sgi.fiis.shared.domain.exception.BusinessException;

public class InvalidStateTransitionException extends BusinessException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }

    public InvalidStateTransitionException(String errorKey, Object... args) {
        super(errorKey, args);
    }
}
