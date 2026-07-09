package com.sgi.fiis.thesis.domain.exception;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;

public class ThesisReportNotFoundException extends ResourceNotFoundException {
    public ThesisReportNotFoundException(Integer id) {
        super("thesis.error.report-not-found", id);
    }
}
