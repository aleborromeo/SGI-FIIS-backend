package com.sgi.fiis.users.application.usecase;

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
@DisplayName("EditUserUseCase Unit Tests")
class EditUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private RoleRepositoryPort roleRepository;

    @InjectMocks
    private EditUserUseCase editUserUseCase;

    @Test
    @DisplayName("Should successfully update user fields when valid")
    void testEditarUsuarioExito() {
        User user = User.builder()
                .id(1L)
                .dni("12345678")
                .firstName("Juan")
                .lastName("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .roleCode("ESTUDIANTE")
                .build();

        Role role = Role.builder().roleCode("DOCENTE_INVESTIGADOR").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByCode("DOCENTE_INVESTIGADOR")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = editUserUseCase.execute(1L, "Juan Carlos", "Perez Gomez", 
                "jc.perez@unas.edu.pe", "999888777", "DOCENTE_INVESTIGADOR");

        assertNotNull(result);
        assertEquals("Juan Carlos", result.getFirstName());
        assertEquals("Perez Gomez", result.getLastName());
        assertEquals("jc.perez@unas.edu.pe", result.getInstitutionalEmail());
        assertEquals("999888777", result.getPhone());
        assertEquals("DOCENTE_INVESTIGADOR", result.getRoleCode());

        verify(userRepository).findById(1L);
        verify(roleRepository).findByCode("DOCENTE_INVESTIGADOR");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user to edit does not exist")
    void testEditarUsuarioNoExiste() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
                editUserUseCase.execute(1L, "Juan", "Perez", null, null, null));

        verify(userRepository).findById(1L);
        verifyNoInteractions(roleRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when new email belongs to another user")
    void testEditarUsuarioCorreoDuplicado() {
        User user = User.builder()
                .id(1L)
                .institutionalEmail("juan.perez@unas.edu.pe")
                .build();

        User anotherUser = User.builder()
                .id(2L)
                .institutionalEmail("duplicado@unas.edu.pe")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("duplicado@unas.edu.pe")).thenReturn(Optional.of(anotherUser));

        assertThrows(DuplicateResourceException.class, () -> 
                editUserUseCase.execute(1L, null, null, "duplicado@unas.edu.pe", null, null));

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("duplicado@unas.edu.pe");
        verifyNoInteractions(roleRepository);
    }

    @Test
    @DisplayName("Should throw BusinessException when editing user with email that does not end with .edu.pe")
    void testEditarUsuarioCorreoInvalido() {
        User user = User.builder()
                .id(1L)
                .institutionalEmail("juan.perez@unas.edu.pe")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> editUserUseCase.execute(1L, null, null, "invalido@gmail.com", null, null)
        );

        assertEquals("The institutional email must belong to the .edu.pe domain", exception.getMessage());
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(roleRepository);
    }
}
