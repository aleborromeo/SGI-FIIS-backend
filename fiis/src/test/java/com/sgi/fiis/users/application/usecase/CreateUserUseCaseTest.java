package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.Role;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.RoleRepositoryPort;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase Unit Tests")
class CreateUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private RoleRepositoryPort roleRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Test
    @DisplayName("Should successfully create a new user and hash their password using DNI")
    void testCrearUsuarioExito() {
        User user = User.builder()
                .dni("12345678")
                .firstName("Carlos")
                .lastName("Santana")
                .roleCode("DOCENTE_INVESTIGADOR")
                .build();

        Role role = Role.builder().roleCode("DOCENTE_INVESTIGADOR").description("Docente").build();

        when(roleRepository.findByCode("DOCENTE_INVESTIGADOR")).thenReturn(Optional.of(role));
        when(userRepository.existsByDni("12345678")).thenReturn(false);
        when(userRepository.existsByEmail("carlos.santana@unas.edu.pe")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = createUserUseCase.execute(user);

        assertNotNull(result);
        assertEquals("carlos.santana@unas.edu.pe", result.getInstitutionalEmail());
        assertEquals("encoded-password", result.getPasswordHash());
        assertTrue(result.isActive());
        assertTrue(result.isMustChangePassword());

        verify(roleRepository).findByCode("DOCENTE_INVESTIGADOR");
        verify(userRepository).existsByDni("12345678");
        verify(userRepository).existsByEmail("carlos.santana@unas.edu.pe");
        verify(passwordEncoder).encode("12345678");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when role does not exist")
    void testCrearUsuarioRolNoExiste() {
        User user = User.builder()
                .dni("12345678")
                .firstName("Carlos")
                .lastName("Santana")
                .roleCode("ROL_INEXISTENTE")
                .build();

        when(roleRepository.findByCode("ROL_INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> createUserUseCase.execute(user));

        verify(roleRepository).findByCode("ROL_INEXISTENTE");
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when DNI already exists")
    void testCrearUsuarioDniDuplicado() {
        User user = User.builder()
                .dni("12345678")
                .firstName("Carlos")
                .lastName("Santana")
                .roleCode("DOCENTE_INVESTIGADOR")
                .build();

        Role role = Role.builder().roleCode("DOCENTE_INVESTIGADOR").build();

        when(roleRepository.findByCode("DOCENTE_INVESTIGADOR")).thenReturn(Optional.of(role));
        when(userRepository.existsByDni("12345678")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> createUserUseCase.execute(user));

        verify(roleRepository).findByCode("DOCENTE_INVESTIGADOR");
        verify(userRepository).existsByDni("12345678");
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should throw BusinessException when email does not end with .edu.pe")
    void testCrearUsuarioCorreoInvalido() {
        User user = User.builder()
                .dni("12345678")
                .firstName("Carlos")
                .lastName("Santana")
                .institutionalEmail("carlos@gmail.com")
                .roleCode("DOCENTE_INVESTIGADOR")
                .build();

        Role role = Role.builder().roleCode("DOCENTE_INVESTIGADOR").build();

        when(roleRepository.findByCode("DOCENTE_INVESTIGADOR")).thenReturn(Optional.of(role));
        when(userRepository.existsByDni("12345678")).thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> createUserUseCase.execute(user)
        );

        assertEquals("The institutional email must belong to the .edu.pe domain", exception.getMessage());
        verify(roleRepository).findByCode("DOCENTE_INVESTIGADOR");
        verify(userRepository).existsByDni("12345678");
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }
}
