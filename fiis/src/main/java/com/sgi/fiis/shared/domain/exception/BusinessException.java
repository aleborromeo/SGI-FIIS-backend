package com.sgi.fiis.shared.domain.exception;

public class BusinessException extends RuntimeException {
    private final Object[] args;

    public BusinessException(String message) {
        super(message);
        this.args = null;
    }

    public BusinessException(String message, Object[] args) {
        super(message);
        this.args = args != null ? args.clone() : null;
    }

    public Object[] getArgs() {
        return args != null ? args.clone() : null;
    }
}
