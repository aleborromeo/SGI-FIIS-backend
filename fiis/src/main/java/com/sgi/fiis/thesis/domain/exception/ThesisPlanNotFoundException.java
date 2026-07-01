package com.sgi.fiis.thesis.domain.exception;

public class ThesisPlanNotFoundException extends RuntimeException {
    public ThesisPlanNotFoundException(Integer id) { super("No existe el plan de tesis con id: " + id); }
}
