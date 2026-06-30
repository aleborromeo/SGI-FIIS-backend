package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerifyRegistrationUseCase Unit Tests")
class VerifyRegistrationUseCaseTest {

    @Mock
    private PendingRegistrationService pendingRegistrationService;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private TokenProviderPort tokenProvider;

    @InjectMocks
    private VerifyRegistrationUseCase verifyRegistrationUseCase;

    @Test
    @DisplayName("Should successfully verify registration code, save active user and return JWT")
    void testVerifyRegistrationSuccess() {
        String email = "juan.perez@unas.edu.pe";
        String code = "123456";

        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail(email)
                .phone("999888777")
                .password("password123")
                .confirmPassword("password123")
                .roleCode("DOCENTE")
                .build();

        PendingRegistrationService.PendingRegistration pending = mock(PendingRegistrationService.PendingRegistration.class);
        when(pending.getRequestDto()).thenReturn(dto);
        when(pending.getCode()).thenReturn(code);
        when(pending.isExpired()).thenReturn(false);

        when(pendingRegistrationService.get(email)).thenReturn(pending);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");

        User mockSavedUser = User.builder()
                .institutionalEmail(email)
                .firstNames("Juan")
                .lastNames("Perez")
                .roleCode("DOCENTE")
                .active(true)
                .mustChangePassword(false)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);
        when(tokenProvider.generateToken(email, "DOCENTE")).thenReturn("jwt-token");

        LoginResponseDto response = verifyRegistrationUseCase.execute(email, code);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(email, response.getEmail());
        assertEquals("DOCENTE", response.getRoleCode());
        assertFalse(response.isMustChangePassword());

        verify(pendingRegistrationService).get(email);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(pendingRegistrationService).remove(email);
        verify(tokenProvider).generateToken(email, "DOCENTE");
    }

    @Test
    @DisplayName("Should throw BusinessException when no pending registration is found")
    void testVerifyRegistrationNotFound() {
        String email = "invalid@unas.edu.pe";
        String code = "123456";

        when(pendingRegistrationService.get(email)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                verifyRegistrationUseCase.execute(email, code));
        assertEquals("auth.register.pending-not-found", ex.getMessage());

        verify(pendingRegistrationService).get(email);
        verifyNoInteractions(passwordEncoder, userRepository, tokenProvider);
    }

    @Test
    @DisplayName("Should throw BusinessException and remove from memory when registration code has expired")
    void testVerifyRegistrationExpired() {
        String email = "juan.perez@unas.edu.pe";
        String code = "123456";

        PendingRegistrationService.PendingRegistration pending = mock(PendingRegistrationService.PendingRegistration.class);
        when(pending.isExpired()).thenReturn(true);

        when(pendingRegistrationService.get(email)).thenReturn(pending);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                verifyRegistrationUseCase.execute(email, code));
        assertEquals("auth.register.expired", ex.getMessage());

        verify(pendingRegistrationService).get(email);
        verify(pendingRegistrationService).remove(email);
        verifyNoInteractions(passwordEncoder, userRepository, tokenProvider);
    }

    @Test
    @DisplayName("Should throw BusinessException when verification code does not match")
    void testVerifyRegistrationCodeMismatch() {
        String email = "juan.perez@unas.edu.pe";
        String code = "123456";
        String wrongCode = "654321";

        PendingRegistrationService.PendingRegistration pending = mock(PendingRegistrationService.PendingRegistration.class);
        when(pending.getCode()).thenReturn(code);
        when(pending.isExpired()).thenReturn(false);

        when(pendingRegistrationService.get(email)).thenReturn(pending);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                verifyRegistrationUseCase.execute(email, wrongCode));
        assertEquals("auth.register.invalid-code", ex.getMessage());

        verify(pendingRegistrationService).get(email);
        verify(pendingRegistrationService, never()).remove(email);
        verifyNoInteractions(passwordEncoder, userRepository, tokenProvider);
    }
}
