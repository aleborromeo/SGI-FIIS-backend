package com.sgi.fiis.thesis.presentation.handler;

import com.sgi.fiis.thesis.domain.exception.BusinessRuleViolationException;
import com.sgi.fiis.thesis.domain.exception.InvalidStateTransitionException;
import com.sgi.fiis.thesis.domain.exception.ThesisPlanNotFoundException;
import com.sgi.fiis.thesis.domain.exception.ThesisReportNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ThesisExceptionHandler Unit Tests")
class ThesisExceptionHandlerTest {

    private ThesisExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ThesisExceptionHandler();
    }

    @Test
    @DisplayName("ThesisPlanNotFoundException returns 404")
    void planNotFoundReturns404() {
        ResponseEntity<ThesisExceptionHandler.ApiError> response =
                handler.notFound(new ThesisPlanNotFoundException(1));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertTrue(response.getBody().message().contains("No existe el plan de tesis"));
    }

    @Test
    @DisplayName("ThesisReportNotFoundException returns 404")
    void reportNotFoundReturns404() {
        ResponseEntity<ThesisExceptionHandler.ApiError> response =
                handler.notFound(new ThesisReportNotFoundException(5));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertTrue(response.getBody().message().contains("No existe el informe de tesis"));
    }

    @Test
    @DisplayName("BusinessRuleViolationException returns 400")
    void businessRuleReturns400() {
        ResponseEntity<ThesisExceptionHandler.ApiError> response =
                handler.business(new BusinessRuleViolationException("Grupo inactivo"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Grupo inactivo", response.getBody().message());
    }

    @Test
    @DisplayName("InvalidStateTransitionException returns 400")
    void invalidTransitionReturns400() {
        ResponseEntity<ThesisExceptionHandler.ApiError> response =
                handler.business(new InvalidStateTransitionException("Transicion invalida"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Transicion invalida", response.getBody().message());
    }
}
