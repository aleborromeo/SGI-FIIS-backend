package com.sgi.fiis.auth.application.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que administra el almacenamiento temporal en memoria de los códigos de restablecimiento de contraseña.
 */
@Service
public class PendingResetPasswordService {

    public static class ResetPasswordCode {
        private final String code;
        private final LocalDateTime expiresAt;

        public ResetPasswordCode(String code) {
            this.code = code;
            this.expiresAt = LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(5); // Expira en 5 mins
        }

        public String getCode() {
            return code;
        }

        public boolean isExpired() {
            return LocalDateTime.now(ZoneId.of("UTC")).isAfter(expiresAt);
        }
    }

    private final Map<String, ResetPasswordCode> store = new ConcurrentHashMap<>();

    public void register(String email, String code) {
        String cleanEmail = email != null ? email.trim().toLowerCase() : "";
        store.put(cleanEmail, new ResetPasswordCode(code));
    }

    public ResetPasswordCode get(String email) {
        String cleanEmail = email != null ? email.trim().toLowerCase() : "";
        return store.get(cleanEmail);
    }

    public void remove(String email) {
        String cleanEmail = email != null ? email.trim().toLowerCase() : "";
        store.remove(cleanEmail);
    }
}
