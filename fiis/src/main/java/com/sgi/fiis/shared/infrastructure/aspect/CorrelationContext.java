package com.sgi.fiis.shared.infrastructure.aspect;

import org.springframework.stereotype.Component;

@Component
public class CorrelationContext {

    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    public void setCorrelationId(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    public String getCorrelationId() {
        return CORRELATION_ID.get();
    }

    public void clear() {
        CORRELATION_ID.remove();
    }
}
