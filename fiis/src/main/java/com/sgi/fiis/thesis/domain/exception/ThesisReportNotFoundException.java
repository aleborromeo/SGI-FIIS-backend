package com.sgi.fiis.thesis.domain.exception;

public class ThesisReportNotFoundException extends RuntimeException {
    public ThesisReportNotFoundException(Integer id) { super("No existe el informe de tesis con id: " + id); }
}
