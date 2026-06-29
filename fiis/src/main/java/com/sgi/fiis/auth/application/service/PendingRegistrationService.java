package com.sgi.fiis.auth.application.service;

import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que administra el almacenamiento temporal en memoria de los registros pendientes.
 * Evita insertar registros no verificados en la base de datos.
 */
@Service
public class PendingRegistrationService {

    public static class PendingRegistration {
        private final RegisterRequestDto requestDto;
        private final String code;
        private final LocalDateTime expiresAt;

        public PendingRegistration(RegisterRequestDto requestDto, String code) {
            this.requestDto = requestDto;
            this.code = code;
            this.expiresAt = LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(5); // Expira en 5 mins
        }

        public RegisterRequestDto getRequestDto() {
            return requestDto;
        }

        public String getCode() {
            return code;
        }

        public boolean isExpired() {
            return LocalDateTime.now(ZoneId.of("UTC")).isAfter(expiresAt);
        }
    }

    private final Map<String, PendingRegistration> store = new ConcurrentHashMap<>();

    public void register(String email, RegisterRequestDto requestDto, String code) {
        String cleanEmail = email != null ? email.trim().toLowerCase() : "";
        store.put(cleanEmail, new PendingRegistration(requestDto, code));
    }

    public PendingRegistration get(String email) {
        String cleanEmail = email != null ? email.trim().toLowerCase() : "";
        return store.get(cleanEmail);
    }

    public void remove(String email) {
        String cleanEmail = email != null ? email.trim().toLowerCase() : "";
        store.remove(cleanEmail);
    }
}
