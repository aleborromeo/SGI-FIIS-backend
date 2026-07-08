package com.sgi.fiis.shared.infrastructure.exception;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Locale resolveLocale() {
        return LocaleContextHolder.getLocale();
    }

    private String translate(String errorKey, Object... args) {
        if (errorKey == null) return null;
        return messageSource.getMessage(errorKey, args, errorKey, resolveLocale());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        String message = translate(ex.getErrorKey(), ex.getArgs());
        if (message == null) message = ex.getMessage();
        return buildResponse(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
        String message = translate(ex.getErrorKey(), ex.getArgs());
        if (message == null) message = ex.getMessage();
        return buildResponse(HttpStatus.CONFLICT, message);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        String message = translate(ex.getErrorKey(), ex.getArgs());
        if (message == null) message = ex.getMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(BusinessRuleValidationException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule(BusinessRuleValidationException ex) {
        String message = translate(ex.getErrorKey(), ex.getArgs());
        if (message == null) message = ex.getMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        String message = messageSource.getMessage("auth.error.bad-credentials", null, "Credenciales inválidas", resolveLocale());
        return buildResponse(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String translatedMsg = messageSource.getMessage(error.getDefaultMessage(), null, error.getDefaultMessage(), resolveLocale());
            errors.put(error.getField(), translatedMsg);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", messageSource.getMessage("shared.validation.error", null, "Errores de validación", resolveLocale()));
        body.put("details", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        String message = messageSource.getMessage("shared.error.internal", null, "Error interno del servidor", resolveLocale());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
