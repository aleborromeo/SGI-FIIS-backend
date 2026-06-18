package com.sgi.fiis.users.application.usecase;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserUseCase Unit Tests")
class GetUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    @Test
    @DisplayName("Should successfully return user by id")
    void testGetUserSuccess() {
        User user = User.builder().id(1L).firstNames("Juan").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = getUserUseCase.execute(1L);
        assertNotNull(result);
        assertEquals("Juan", result.getFirstNames());

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user by id does not exist")
    void testGetUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getUserUseCase.execute(1L));

        verify(userRepository).findById(1L);
    }
}
