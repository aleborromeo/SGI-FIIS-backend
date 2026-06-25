package com.sgi.fiis.shared.domain.exception;

public class BusinessRuleValidationException extends RuntimeException {

    private final String errorKey;
    private final transient Object[] args;

    public BusinessRuleValidationException(String message) {
        super(message);
        this.errorKey = null;
        this.args = null;
    }

    public BusinessRuleValidationException(String errorKey, Object... args) {
        super(errorKey);
        this.errorKey = errorKey;
        this.args = args;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public Object[] getArgs() {
        return args != null ? args.clone() : null;
    }
}
