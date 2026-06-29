package com.sgi.fiis.shared.infrastructure.exception;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler(messageSource);
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void handleNotFound_shouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("exception.not-found");
        when(messageSource.getMessage(eq("exception.not-found"), any(), eq("exception.not-found"), any(Locale.class)))
                .thenReturn("Recurso no encontrado");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNotFound(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Recurso no encontrado", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle DuplicateResourceException without fields")
    void handleDuplicate_withoutFields_shouldReturn409() {
        DuplicateResourceException ex = new DuplicateResourceException("exception.duplicate");
        when(messageSource.getMessage(eq("exception.duplicate"), any(), eq("exception.duplicate"), any(Locale.class)))
                .thenReturn("Recurso duplicado");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDuplicate(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Recurso duplicado", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Should handle DuplicateResourceException with fields")
    void handleDuplicate_withFields_shouldReturn409() {
        DuplicateResourceException ex = new DuplicateResourceException("Usuario", "DNI", "12345678");
        when(messageSource.getMessage(eq("exception.duplicate-resource"), any(), anyString(), any(Locale.class)))
                .thenReturn("Usuario con DNI 12345678 ya existe");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDuplicate(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Usuario con DNI 12345678 ya existe", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Should handle BusinessException")
    void handleBusiness_shouldReturn400() {
        BusinessException ex = new BusinessException("exception.business", new Object[]{"arg1"});
        when(messageSource.getMessage(eq("exception.business"), any(), eq("exception.business"), any(Locale.class)))
                .thenReturn("Error de negocio");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBusiness(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Error de negocio", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Should handle BadCredentialsException")
    void handleBadCredentials_shouldReturn401() {
        BadCredentialsException ex = new BadCredentialsException("auth.invalid-credentials");
        when(messageSource.getMessage(eq("auth.invalid-credentials"), any(), eq("auth.invalid-credentials"), any(Locale.class)))
                .thenReturn("Credenciales inválidas");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBadCredentials(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Credenciales inválidas", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void handleValidation_shouldReturn400WithDetails() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "email", "Formato de correo inválido");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        when(messageSource.getMessage(eq("validation.error-title"), any(), anyString(), any(Locale.class)))
                .thenReturn("Errores de validación");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidation(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Errores de validación", body.get("error"));
        
        Map<String, String> details = (Map<String, String>) body.get("details");
        assertNotNull(details);
        assertEquals("Formato de correo inválido", details.get("email"));
    }

    @Test
    @DisplayName("Should handle MailException")
    void handleMailException_shouldReturn502() {
        org.springframework.mail.MailException ex = new org.springframework.mail.MailException("Mail error") {
            @Override
            public Throwable getRootCause() {
                return new RuntimeException("SMTP Connection timed out");
            }
        };

        when(messageSource.getMessage(eq("exception.mail-failed"), any(), anyString(), any(Locale.class)))
                .thenReturn("Error al enviar correo (SMTP)");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleMailException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals(502, response.getBody().get("status"));
        assertEquals("Error al enviar correo (SMTP)", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void handleGeneral_shouldReturn500() {
        Exception ex = new Exception("unexpected error");
        when(messageSource.getMessage(eq("exception.internal-error"), any(), anyString(), any(Locale.class)))
                .thenReturn("Error interno del servidor");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneral(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Error interno del servidor", response.getBody().get("message"));
    }
    @Test
    @DisplayName("Should handle AuthorizationDeniedException")
    void handleAuthorizationDenied_shouldReturn403() {
        org.springframework.security.authorization.AuthorizationDeniedException ex = 
            new org.springframework.security.authorization.AuthorizationDeniedException("Access Denied");
            
        when(messageSource.getMessage(eq("exception.access-denied"), any(), anyString(), any(Locale.class)))
                .thenReturn("Acceso denegado");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().get("status"));
        assertEquals("Acceso denegado", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Should handle AccessDeniedException")
    void handleAccessDenied_shouldReturn403() {
        org.springframework.security.access.AccessDeniedException ex = 
            new org.springframework.security.access.AccessDeniedException("Access Denied");
            
        when(messageSource.getMessage(eq("exception.access-denied"), any(), anyString(), any(Locale.class)))
                .thenReturn("Acceso denegado");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAccessDenied(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().get("status"));
        assertEquals("Acceso denegado", response.getBody().get("message"));
    }
}
