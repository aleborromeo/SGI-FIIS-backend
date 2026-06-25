package com.sgi.fiis.shared.domain.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String errorKey;
    private final transient Object[] args;

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorKey = null;
        this.args = null;
    }

    public ResourceNotFoundException(String errorKey, Object... args) {
        super(errorKey);
        this.errorKey = errorKey;
        this.args = args;
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s no encontrado con %s: '%s'", resource, field, value));
        this.errorKey = null;
        this.args = null;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public Object[] getArgs() {
        return args != null ? args.clone() : null;
    }
}
