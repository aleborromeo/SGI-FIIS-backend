package com.sgi.fiis.auth.application.service;

import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService.PendingRegistration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PendingRegistrationService Unit Tests")
class PendingRegistrationServiceTest {

    private final PendingRegistrationService service = new PendingRegistrationService();

    @Test
    @DisplayName("Should register and get pending registration successfully")
    void testRegisterAndGet() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .phone("987654321")
                .roleCode("DOCENTE")
                .build();

        String code = "123456";
        String email = "JUAN.PEREZ@unas.edu.pe";

        service.register(email, dto, code);

        PendingRegistration registration = service.get("juan.perez@unas.edu.pe");

        assertNotNull(registration);
        assertEquals(dto, registration.getRequestDto());
        assertEquals(code, registration.getCode());
        assertFalse(registration.isExpired());
    }

    @Test
    @DisplayName("Should return null when getting non-existent registration")
    void testGetNonExistent() {
        assertNull(service.get("nonexistent@unas.edu.pe"));
    }

    @Test
    @DisplayName("Should remove registration successfully")
    void testRemove() {
        RegisterRequestDto dto = new RegisterRequestDto();
        service.register("remove@unas.edu.pe", dto, "123456");

        assertNotNull(service.get("remove@unas.edu.pe"));

        service.remove("REMOVE@unas.edu.pe");

        assertNull(service.get("remove@unas.edu.pe"));
    }

    @Test
    @DisplayName("Should detect expired registration")
    void testIsExpired() {
        RegisterRequestDto dto = new RegisterRequestDto();
        String code = "123456";

        PendingRegistration registration = new PendingRegistration(dto, code);
        assertFalse(registration.isExpired());

        LocalDateTime futureTime = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(6);

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            mockedLocalDateTime.when(() -> LocalDateTime.now(any(ZoneId.class))).thenReturn(futureTime);
            assertTrue(registration.isExpired());
        }
    }
}
