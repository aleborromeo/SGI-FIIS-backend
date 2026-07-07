package com.sgi.fiis.shared.infrastructure.exception;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale);
        return buildResponse(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message;
        if (ex.getResource() != null) {
            Object[] args = new Object[]{ex.getResource(), ex.getField(), ex.getValue()};
            message = messageSource.getMessage("exception.duplicate-resource", args, ex.getMessage(), locale);
        } else {
            message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale);
        }
        return buildResponse(HttpStatus.CONFLICT, message);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(ex.getMessage(), ex.getArgs(), ex.getMessage(), locale);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(BusinessRuleValidationException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRuleValidation(BusinessRuleValidationException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = ex.getErrorKey() != null 
                ? messageSource.getMessage(ex.getErrorKey(), ex.getArgs(), ex.getMessage(), locale)
                : ex.getMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), locale);
        return buildResponse(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        String errorTitle = messageSource.getMessage("validation.error-title", null, "Errores de validación", locale);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now(java.time.ZoneId.systemDefault()).toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", errorTitle);
        body.put("details", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(org.springframework.mail.MailException.class)
    public ResponseEntity<Map<String, Object>> handleMailException(org.springframework.mail.MailException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String detail = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        Object[] args = new Object[]{detail};
        String message = messageSource.getMessage("exception.mail-failed", args, "Error al enviar correo (SMTP): " + detail, locale);
        return buildResponse(HttpStatus.BAD_GATEWAY, message);
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(org.springframework.security.authorization.AuthorizationDeniedException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("exception.access-denied", null, "Acceso denegado. No tienes los permisos necesarios para realizar esta acción.", locale);
        return buildResponse(HttpStatus.FORBIDDEN, message);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("exception.access-denied", null, "Acceso denegado. No tienes los permisos necesarios para realizar esta acción.", locale);
        return buildResponse(HttpStatus.FORBIDDEN, message);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentAndState(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Excepción no manejada capturada: ", ex);
        
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("exception.internal-error", null, "Error interno del servidor", locale);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now(java.time.ZoneId.systemDefault()).toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
