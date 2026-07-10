package com.sgi.fiis.shared.infrastructure.exception;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.domain.model.InvalidTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MessageSource messageSource;

    @BeforeEach
    void setup() {
        messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(any(String.class), any(), any(String.class), any())).thenAnswer(invocation -> invocation.getArgument(2));
        exceptionHandler = new GlobalExceptionHandler(messageSource);
    }

    @Test
    void handleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().get("message"));
    }

    @Test
    void handleDuplicate() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDuplicate(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate", response.getBody().get("message"));
    }

    @Test
    void handleBusiness() {
        BusinessException ex = new BusinessException("Business");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBusiness(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Business", response.getBody().get("message"));
    }

    @Test
    void handleBadCredentials() {
        BadCredentialsException ex = new BadCredentialsException("Bad");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBadCredentials(ex);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody().get("message"));
    }

    @Test
    void handleValidation() {
        org.springframework.core.MethodParameter methodParameter = mock(org.springframework.core.MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "Error message");
        
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Errores de validación", response.getBody().get("error"));
    }

    @Test
    void handleGeneral() {
        Exception ex = new Exception("General");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneral(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody().get("message"));
    }

    @Test
    void handleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().get("message"));
    }

    @Test
    void handleIllegalState() {
        IllegalStateException ex = new IllegalStateException("Invalid state");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalState(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid state", response.getBody().get("message"));
    }

    @Test
    void handleBusinessRule() {
        BusinessRuleValidationException ex = new BusinessRuleValidationException("rule.violation");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBusinessRule(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("rule.violation", response.getBody().get("message"));
    }

    @Test
    void handleInvalidTransition() {
        InvalidTransitionException ex = new InvalidTransitionException("Invalid transition");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidTransition(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid transition", response.getBody().get("message"));
    }

    @Test
    void handleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acceso denegado", response.getBody().get("message"));
    }
}
