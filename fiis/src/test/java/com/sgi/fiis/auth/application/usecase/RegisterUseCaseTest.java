package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
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
@DisplayName("RegisterUseCase Unit Tests")
class RegisterUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PendingRegistrationService pendingRegistrationService;

    @Mock
    private EmailSenderPort emailSender;

    @InjectMocks
    private RegisterUseCase registerUseCase;

    @Test
    @DisplayName("Should successfully store pending registration and send verification code when data is valid")
    void testRegisterSuccess() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .phone("999888777")
                .password("password123")
                .confirmPassword("password123")
                .roleCode("DOCENTE")
                .build();

        when(userRepository.existsByDni("12345678")).thenReturn(false);
        when(userRepository.existsByEmail("juan.perez@unas.edu.pe")).thenReturn(false);
        doNothing().when(pendingRegistrationService).register(eq("juan.perez@unas.edu.pe"), eq(dto), anyString());
        doNothing().when(emailSender).sendVerificationCode(eq("juan.perez@unas.edu.pe"), anyString());

        assertDoesNotThrow(() -> registerUseCase.execute(dto));

        verify(userRepository).existsByDni("12345678");
        verify(userRepository).existsByEmail("juan.perez@unas.edu.pe");
        verify(pendingRegistrationService).register(eq("juan.perez@unas.edu.pe"), eq(dto), anyString());
        verify(emailSender).sendVerificationCode(eq("juan.perez@unas.edu.pe"), anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when email is not from .edu.pe domain")
    void testRegisterInvalidDomain() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@gmail.com")
                .phone("999888777")
                .password("password123")
                .confirmPassword("password123")
                .roleCode("DOCENTE")
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> registerUseCase.execute(dto));
        assertEquals("auth.email.invalid-domain", ex.getMessage());

        verifyNoInteractions(userRepository, pendingRegistrationService, emailSender);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when DNI already exists in database")
    void testRegisterDuplicateDni() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .phone("999888777")
                .password("password123")
                .confirmPassword("password123")
                .roleCode("DOCENTE")
                .build();

        when(userRepository.existsByDni("12345678")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> registerUseCase.execute(dto));

        verify(userRepository).existsByDni("12345678");
        verify(userRepository, never()).existsByEmail(anyString());
        verifyNoInteractions(pendingRegistrationService, emailSender);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists in database")
    void testRegisterDuplicateEmail() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .phone("999888777")
                .password("password123")
                .confirmPassword("password123")
                .roleCode("DOCENTE")
                .build();

        when(userRepository.existsByDni("12345678")).thenReturn(false);
        when(userRepository.existsByEmail("juan.perez@unas.edu.pe")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> registerUseCase.execute(dto));

        verify(userRepository).existsByDni("12345678");
        verify(userRepository).existsByEmail("juan.perez@unas.edu.pe");
        verifyNoInteractions(pendingRegistrationService, emailSender);
    }

    @Test
    @DisplayName("Should throw BusinessException when passwords do not match")
    void testRegisterPasswordMismatch() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .phone("999888777")
                .password("password123")
                .confirmPassword("password_diferente")
                .roleCode("DOCENTE")
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> registerUseCase.execute(dto));
        assertEquals("auth.password.mismatch", ex.getMessage());

        verifyNoInteractions(userRepository, pendingRegistrationService, emailSender);
    }
}
