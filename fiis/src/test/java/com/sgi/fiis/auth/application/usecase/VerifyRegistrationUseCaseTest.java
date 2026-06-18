package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.Usuario;
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
@DisplayName("VerifyRegistrationUseCase Unit Tests")
class VerifyRegistrationUseCaseTest {

    @Mock
    private PendingRegistrationService pendingRegistrationService;

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private TokenProviderPort tokenProvider;

    @InjectMocks
    private VerifyRegistrationUseCase verifyRegistrationUseCase;

    @Test
    @DisplayName("Should successfully verify registration code, save active user and return JWT")
    void testVerifyRegistrationSuccess() {
        String correo = "juan.perez@unas.edu.pe";
        String codigo = "123456";

        RegisterRequestDto dto = RegisterRequestDto.builder()
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Perez")
                .correoInstitucional(correo)
                .telefono("999888777")
                .password("password123")
                .confirmarPassword("password123")
                .rolCodigo("DOCENTE")
                .build();

        PendingRegistrationService.PendingRegistration pending = mock(PendingRegistrationService.PendingRegistration.class);
        when(pending.getRequestDto()).thenReturn(dto);
        when(pending.getCode()).thenReturn(codigo);
        when(pending.isExpired()).thenReturn(false);

        when(pendingRegistrationService.get(correo)).thenReturn(pending);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");
        
        Usuario mockSavedUser = Usuario.builder()
                .correoInstitucional(correo)
                .nombres("Juan")
                .apellidos("Perez")
                .rolCodigo("DOCENTE")
                .activo(true)
                .mustChangePassword(false)
                .build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(mockSavedUser);
        when(tokenProvider.generateToken(correo, "DOCENTE")).thenReturn("jwt-token");

        LoginResponseDto response = verifyRegistrationUseCase.execute(correo, codigo);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals(correo, response.getCorreo());
        assertEquals("DOCENTE", response.getRolCodigo());
        assertFalse(response.isMustChangePassword());

        verify(pendingRegistrationService).get(correo);
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(pendingRegistrationService).remove(correo);
        verify(tokenProvider).generateToken(correo, "DOCENTE");
    }

    @Test
    @DisplayName("Should throw BusinessException when no pending registration is found")
    void testVerifyRegistrationNotFound() {
        String correo = "invalid@unas.edu.pe";
        String codigo = "123456";

        when(pendingRegistrationService.get(correo)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> 
                verifyRegistrationUseCase.execute(correo, codigo));
        assertEquals("No se encontró ningún registro pendiente o ya ha sido verificado", ex.getMessage());

        verify(pendingRegistrationService).get(correo);
        verifyNoInteractions(passwordEncoder, usuarioRepository, tokenProvider);
    }

    @Test
    @DisplayName("Should throw BusinessException and remove from memory when registration code has expired")
    void testVerifyRegistrationExpired() {
        String correo = "juan.perez@unas.edu.pe";
        String codigo = "123456";

        PendingRegistrationService.PendingRegistration pending = mock(PendingRegistrationService.PendingRegistration.class);
        when(pending.isExpired()).thenReturn(true);

        when(pendingRegistrationService.get(correo)).thenReturn(pending);

        BusinessException ex = assertThrows(BusinessException.class, () -> 
                verifyRegistrationUseCase.execute(correo, codigo));
        assertEquals("El código de verificación ha expirado", ex.getMessage());

        verify(pendingRegistrationService).get(correo);
        verify(pendingRegistrationService).remove(correo);
        verifyNoInteractions(passwordEncoder, usuarioRepository, tokenProvider);
    }

    @Test
    @DisplayName("Should throw BusinessException when verification code does not match")
    void testVerifyRegistrationCodeMismatch() {
        String correo = "juan.perez@unas.edu.pe";
        String codigo = "123456";
        String wrongCodigo = "654321";

        PendingRegistrationService.PendingRegistration pending = mock(PendingRegistrationService.PendingRegistration.class);
        when(pending.getCode()).thenReturn(codigo);
        when(pending.isExpired()).thenReturn(false);

        when(pendingRegistrationService.get(correo)).thenReturn(pending);

        BusinessException ex = assertThrows(BusinessException.class, () -> 
                verifyRegistrationUseCase.execute(correo, wrongCodigo));
        assertEquals("Código de verificación inválido", ex.getMessage());

        verify(pendingRegistrationService).get(correo);
        verify(pendingRegistrationService, never()).remove(correo);
        verifyNoInteractions(passwordEncoder, usuarioRepository, tokenProvider);
    }
}
