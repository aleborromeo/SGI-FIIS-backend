package com.sgi.fiis.thesis.presentation.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.sgi.fiis.thesis.domain.exception.*;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;

@RestControllerAdvice(basePackages = "com.sgi.fiis.thesis")
public class ThesisExceptionHandler {

    private final MessageSource messageSource;

    public ThesisExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Locale resolveLocale() {
        return LocaleContextHolder.getLocale();
    }

    private String translate(String errorKey, Object[] args, String defaultMessage) {
        if (errorKey == null) return defaultMessage;
        return messageSource.getMessage(errorKey, args, defaultMessage, resolveLocale());
    }

    @ExceptionHandler({ThesisPlanNotFoundException.class, ThesisReportNotFoundException.class})
    public ResponseEntity<ApiError> notFound(ResourceNotFoundException ex) {
        String message = translate(ex.getErrorKey(), ex.getArgs(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(LocalDateTime.now(java.time.ZoneId.systemDefault()), 404, message));
    }

    @ExceptionHandler({BusinessRuleViolationException.class, InvalidStateTransitionException.class})
    public ResponseEntity<ApiError> businessRule(BusinessRuleValidationException ex) {
        String message = translate(ex.getErrorKey(), ex.getArgs(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiError(LocalDateTime.now(java.time.ZoneId.systemDefault()), 400, message));
    }

    @ExceptionHandler(PlanAccessDeniedException.class)
    public ResponseEntity<ApiError> forbidden(PlanAccessDeniedException ex) {
        String message = translate(ex.getMessage(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError(LocalDateTime.now(java.time.ZoneId.systemDefault()), 403, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now(java.time.ZoneId.systemDefault()));
        body.put("status", 400);
        
        String validationErrorMsg = messageSource.getMessage("shared.validation.error", null, "Error de validación", resolveLocale());
        body.put("message", validationErrorMsg);
        
        body.put("errors", ex.getBindingResult().getFieldErrors().stream()
                .map(err -> {
                    String fieldMessage = messageSource.getMessage(err.getDefaultMessage(), null, err.getDefaultMessage(), resolveLocale());
                    return err.getField() + ": " + fieldMessage;
                }).toList());
        return ResponseEntity.badRequest().body(body);
    }

    public record ApiError(LocalDateTime timestamp, int status, String message) {}
}
