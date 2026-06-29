package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.User;
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
@DisplayName("ChangeUserStatusUseCase Unit Tests")
class ChangeUserStatusUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private ChangeUserStatusUseCase changeUserStatusUseCase;

    @Test
    @DisplayName("Should successfully activate a user")
    void testActivarUsuarioExito() {
        User user = User.builder()
                .id(1L)
                .active(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = changeUserStatusUseCase.execute(1L, true, 2L);

        assertNotNull(result);
        assertTrue(result.isActive());

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should successfully deactivate a user")
    void testDesactivarUsuarioExito() {
        User user = User.builder()
                .id(1L)
                .active(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = changeUserStatusUseCase.execute(1L, false, 2L);

        assertNotNull(result);
        assertFalse(result.isActive());

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw BusinessException when admin attempts to deactivate themselves")
    void testDesactivarAutodeactivacionThrows() {
        assertThrows(BusinessException.class, () -> 
                changeUserStatusUseCase.execute(1L, false, 1L));

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user is not found")
    void testCambiarEstadoUsuarioNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
                changeUserStatusUseCase.execute(1L, true, 2L));

        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }
}
