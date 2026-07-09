package com.sgi.fiis.thesis.presentation.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.sgi.fiis.thesis.domain.exception.*;

@RestControllerAdvice(basePackages = "com.sgi.fiis.thesis")
public class ThesisExceptionHandler {
    @ExceptionHandler({ThesisPlanNotFoundException.class, ThesisReportNotFoundException.class})
    public ResponseEntity<ApiError> notFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(LocalDateTime.now(java.time.ZoneId.systemDefault()), 404, ex.getMessage()));
    }

    @ExceptionHandler({BusinessRuleViolationException.class, InvalidStateTransitionException.class})
    public ResponseEntity<ApiError> business(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ApiError(LocalDateTime.now(java.time.ZoneId.systemDefault()), 400, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now(java.time.ZoneId.systemDefault()));
        body.put("status", 400);
        body.put("message", "Error de validación");
        body.put("errors", ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage()).toList());
        return ResponseEntity.badRequest().body(body);
    }

    public record ApiError(LocalDateTime timestamp, int status, String message) {}
}
