package com.sgi.fiis.auth.application.service;

import com.sgi.fiis.auth.application.service.PendingResetPasswordService.ResetPasswordCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PendingResetPasswordService Unit Tests")
class PendingResetPasswordServiceTest {

    private final PendingResetPasswordService service = new PendingResetPasswordService();

    @Test
    @DisplayName("Should register and get code successfully")
    void testRegisterAndGet() {
        String email = "USER@unas.edu.pe";
        String code = "123456";

        service.register(email, code);

        ResetPasswordCode resetCode = service.get("user@unas.edu.pe");

        assertNotNull(resetCode);
        assertEquals(code, resetCode.getCode());
        assertFalse(resetCode.isExpired());
    }

    @Test
    @DisplayName("Should return null when getting non-existent code")
    void testGetNonExistent() {
        assertNull(service.get("nonexistent@unas.edu.pe"));
    }

    @Test
    @DisplayName("Should remove code successfully")
    void testRemove() {
        service.register("remove@unas.edu.pe", "123456");

        assertNotNull(service.get("remove@unas.edu.pe"));

        service.remove("REMOVE@unas.edu.pe");

        assertNull(service.get("remove@unas.edu.pe"));
    }

    @Test
    @DisplayName("Should detect expired code")
    void testIsExpired() throws Exception {
        ResetPasswordCode resetCode = new ResetPasswordCode("123456");
        assertFalse(resetCode.isExpired());

        Field expiresAtField = ResetPasswordCode.class.getDeclaredField("expiresAt");
        expiresAtField.setAccessible(true);
        expiresAtField.set(resetCode, LocalDateTime.now().minusMinutes(1));

        assertTrue(resetCode.isExpired());
    }
}
