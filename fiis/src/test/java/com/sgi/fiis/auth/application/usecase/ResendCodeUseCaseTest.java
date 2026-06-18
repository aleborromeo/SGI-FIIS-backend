package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.dto.ResendCodeRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResendCodeUseCase Unit Tests")
class ResendCodeUseCaseTest {

    @Mock
    private PendingRegistrationService pendingRegistrationService;

    @Mock
    private EmailSenderPort emailSender;

    @InjectMocks
    private ResendCodeUseCase resendCodeUseCase;

    @Test
    @DisplayName("Should successfully regenerate code and send email when pending registration exists")
    void testResendCodeSuccess() {
        String email = "juan.perez@unas.edu.pe";
        RegisterRequestDto mockDto = RegisterRequestDto.builder()
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Perez")
                .correoInstitucional(email)
                .telefono("999888777")
                .password("password123")
                .confirmarPassword("password123")
                .rolCodigo("DOCENTE")
                .build();

        PendingRegistrationService.PendingRegistration mockPending = 
                new PendingRegistrationService.PendingRegistration(mockDto, "111111");

        when(pendingRegistrationService.get(email)).thenReturn(mockPending);
        doNothing().when(pendingRegistrationService).register(eq(email), eq(mockDto), anyString());
        doNothing().when(emailSender).sendVerificationCode(eq(email), anyString());

        ResendCodeRequestDto requestDto = ResendCodeRequestDto.builder()
                .correo(email)
                .build();

        assertDoesNotThrow(() -> resendCodeUseCase.execute(requestDto));

        verify(pendingRegistrationService).get(email);
        verify(pendingRegistrationService).register(eq(email), eq(mockDto), anyString());
        verify(emailSender).sendVerificationCode(eq(email), anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when no pending registration is found")
    void testResendCodeNotFound() {
        String email = "unknown@unas.edu.pe";
        when(pendingRegistrationService.get(email)).thenReturn(null);

        ResendCodeRequestDto requestDto = ResendCodeRequestDto.builder()
                .correo(email)
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> resendCodeUseCase.execute(requestDto));
        assertEquals("No se encontró ningún registro pendiente para el correo especificado", ex.getMessage());

        verify(pendingRegistrationService).get(email);
        verify(pendingRegistrationService, never()).register(anyString(), any(), anyString());
        verifyNoInteractions(emailSender);
    }
}
