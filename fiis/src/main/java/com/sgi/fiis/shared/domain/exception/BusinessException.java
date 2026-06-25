package com.sgi.fiis.shared.domain.exception;

public class BusinessException extends RuntimeException {

    private final String errorKey;
    private final transient Object[] args;

    public BusinessException(String message) {
        super(message);
        this.errorKey = null;
        this.args = null;
    }

    public BusinessException(String errorKey, Object... args) {
        super(errorKey);
        this.errorKey = errorKey;
        this.args = args;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public Object[] getArgs() {
        return args;
    }
}
