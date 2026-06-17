package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
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
    private UsuarioRepositoryPort usuarioRepository;

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
                .nombres("Juan")
                .apellidos("Perez")
                .correoInstitucional("juan.perez@unas.edu.pe")
                .telefono("999888777")
                .password("password123")
                .rolCodigo("DOCENTE")
                .build();

        when(usuarioRepository.existsByDni("12345678")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("juan.perez@unas.edu.pe")).thenReturn(false);
        doNothing().when(pendingRegistrationService).register(eq("juan.perez@unas.edu.pe"), eq(dto), anyString());
        doNothing().when(emailSender).sendVerificationCode(eq("juan.perez@unas.edu.pe"), anyString());

        assertDoesNotThrow(() -> registerUseCase.execute(dto));

        verify(usuarioRepository).existsByDni("12345678");
        verify(usuarioRepository).existsByCorreo("juan.perez@unas.edu.pe");
        verify(pendingRegistrationService).register(eq("juan.perez@unas.edu.pe"), eq(dto), anyString());
        verify(emailSender).sendVerificationCode(eq("juan.perez@unas.edu.pe"), anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when email is not from .edu.pe domain")
    void testRegisterInvalidDomain() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Perez")
                .correoInstitucional("juan.perez@gmail.com")
                .telefono("999888777")
                .password("password123")
                .rolCodigo("DOCENTE")
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> registerUseCase.execute(dto));
        assertEquals("El correo institucional debe pertenecer al dominio .edu.pe", ex.getMessage());

        verifyNoInteractions(usuarioRepository, pendingRegistrationService, emailSender);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when DNI already exists in database")
    void testRegisterDuplicateDni() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Perez")
                .correoInstitucional("juan.perez@unas.edu.pe")
                .telefono("999888777")
                .password("password123")
                .rolCodigo("DOCENTE")
                .build();

        when(usuarioRepository.existsByDni("12345678")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> registerUseCase.execute(dto));

        verify(usuarioRepository).existsByDni("12345678");
        verify(usuarioRepository, never()).existsByCorreo(anyString());
        verifyNoInteractions(pendingRegistrationService, emailSender);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists in database")
    void testRegisterDuplicateEmail() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Perez")
                .correoInstitucional("juan.perez@unas.edu.pe")
                .telefono("999888777")
                .password("password123")
                .rolCodigo("DOCENTE")
                .build();

        when(usuarioRepository.existsByDni("12345678")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("juan.perez@unas.edu.pe")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> registerUseCase.execute(dto));

        verify(usuarioRepository).existsByDni("12345678");
        verify(usuarioRepository).existsByCorreo("juan.perez@unas.edu.pe");
        verifyNoInteractions(pendingRegistrationService, emailSender);
    }
}
