package com.sgi.fiis.auth.infrastructure.security;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
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
@DisplayName("OAuthUserHandlerAdapter Unit Tests")
class OAuthUserHandlerAdapterTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private OAuthUserHandlerAdapter adapter;

    @Test
    @DisplayName("Should return existing user from OAuth")
    void testFindOrCreateFromOAuthExisting() {
        User existingUser = User.builder()
                .id(1L)
                .institutionalEmail("existing@unas.edu.pe")
                .roleCode("ADMIN")
                .build();

        when(userRepository.findByEmail("existing@unas.edu.pe")).thenReturn(Optional.of(existingUser));

        User result = adapter.findOrCreateFromOAuth("existing@unas.edu.pe", "Existing User", "microsoft");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("existing@unas.edu.pe", result.getInstitutionalEmail());
        verify(userRepository).findByEmail("existing@unas.edu.pe");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Should create new user from OAuth if not exists")
    void testFindOrCreateFromOAuthCreateNew() {
        when(userRepository.findByEmail("new@unas.edu.pe")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-placeholder-password");

        User savedUser = User.builder()
                .id(100L)
                .dni("OA123456")
                .firstNames("New")
                .lastNames("User")
                .institutionalEmail("new@unas.edu.pe")
                .roleCode("ESTUDIANTE")
                .active(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = adapter.findOrCreateFromOAuth("new@unas.edu.pe", "New User", "microsoft");

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("new@unas.edu.pe", result.getInstitutionalEmail());
        verify(userRepository).findByEmail("new@unas.edu.pe");
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
    }
}
