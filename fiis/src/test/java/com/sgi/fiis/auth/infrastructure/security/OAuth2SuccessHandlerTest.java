package com.sgi.fiis.auth.infrastructure.security;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.application.usecase.OAuthLoginUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2SuccessHandler Unit Tests")
class OAuth2SuccessHandlerTest {

    @Mock
    private OAuthLoginUseCase oAuthLoginUseCase;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OidcUser oidcUser;

    @Mock
    private RedirectStrategy redirectStrategy;

    @InjectMocks
    private OAuth2SuccessHandler handler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "frontendUrl", "http://localhost:3000");
        handler.setRedirectStrategy(redirectStrategy);
    }

    @Test
    @DisplayName("Should handle successful OAuth2 authentication and redirect")
    void testOnAuthenticationSuccess() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn("user@unas.edu.pe");
        when(oidcUser.getFullName()).thenReturn("Juan Perez");

        LoginResponseDto loginResponse = LoginResponseDto.builder()
                .token("jwt-token")
                .correo("user@unas.edu.pe")
                .nombres("Juan")
                .apellidos("Perez")
                .rolCodigo("ESTUDIANTE")
                .build();

        when(oAuthLoginUseCase.execute("user@unas.edu.pe", "Juan Perez", "microsoft")).thenReturn(loginResponse);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(oAuthLoginUseCase).execute("user@unas.edu.pe", "Juan Perez", "microsoft");
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), anyString());
    }

    @Test
    @DisplayName("Should extract preferred username when email is null")
    void testOnAuthenticationSuccessPreferredUsername() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(null);
        when(oidcUser.getPreferredUsername()).thenReturn("user-pref@unas.edu.pe");
        when(oidcUser.getFullName()).thenReturn("Juan Perez");

        LoginResponseDto loginResponse = LoginResponseDto.builder()
                .token("jwt-token")
                .correo("user-pref@unas.edu.pe")
                .nombres("Juan")
                .apellidos("Perez")
                .rolCodigo("ESTUDIANTE")
                .build();

        when(oAuthLoginUseCase.execute("user-pref@unas.edu.pe", "Juan Perez", "microsoft")).thenReturn(loginResponse);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(oAuthLoginUseCase).execute("user-pref@unas.edu.pe", "Juan Perez", "microsoft");
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), anyString());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when no email/username can be found")
    void testOnAuthenticationSuccessNoEmailThrows() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn(null);
        when(oidcUser.getPreferredUsername()).thenReturn(null);
        when(oidcUser.getClaimAsString("upn")).thenReturn(null);
        when(oidcUser.getClaimAsString("email")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                handler.onAuthenticationSuccess(request, response, authentication));

        verifyNoInteractions(oAuthLoginUseCase, redirectStrategy);
    }
}
