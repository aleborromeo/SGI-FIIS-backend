package com.sgi.fiis.documentacion.application.exception;

public class DocumentAccessDeniedException extends RuntimeException {
    public DocumentAccessDeniedException(String message) { super(message); }
}