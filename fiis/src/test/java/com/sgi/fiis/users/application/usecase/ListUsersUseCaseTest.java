package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListUsersUseCase Unit Tests")
class ListUsersUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private ListUsersUseCase listUsersUseCase;

    @Test
    @DisplayName("Should call findAll when query is null or blank")
    void testListarTodos() {
        User user = User.builder().id(1L).firstName("Juan").build();
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<User> result = listUsersUseCase.execute(null);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());

        List<User> resultBlank = listUsersUseCase.execute("   ");
        assertEquals(1, resultBlank.size());

        verify(userRepository, times(2)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should call search with trimmed query when query is valid")
    void testBuscarConQuery() {
        User user = User.builder().id(1L).firstName("Juan").build();
        when(userRepository.search("Juan")).thenReturn(Collections.singletonList(user));

        List<User> result = listUsersUseCase.execute(" Juan  ");
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());

        verify(userRepository).search("Juan");
        verifyNoMoreInteractions(userRepository);
    }
}
