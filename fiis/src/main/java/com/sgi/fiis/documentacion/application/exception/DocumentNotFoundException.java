package com.sgi.fiis.documentacion.application.exception;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) { super(message); }
}