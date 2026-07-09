package com.sgi.fiis.thesis.domain.exception;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;

public class ThesisPlanNotFoundException extends ResourceNotFoundException {
    public ThesisPlanNotFoundException(Integer id) {
        super("thesis.error.plan-not-found", id);
    }
}
