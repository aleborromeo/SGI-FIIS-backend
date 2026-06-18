package com.sgi.fiis.shared.domain.exception;

public class DuplicateResourceException extends RuntimeException {
    private final String resource;
    private final String field;
    private final Object value;

    public DuplicateResourceException(String message) {
        super(message);
        this.resource = null;
        this.field = null;
        this.value = null;
    }

    public DuplicateResourceException(String resource, String field, Object value) {
        super("exception.duplicate-resource");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }

    public String getResource() {
        return resource;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
