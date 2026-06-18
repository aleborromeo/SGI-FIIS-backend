package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.domain.port.OAuthUserHandlerPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.users.domain.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuthLoginUseCase Unit Tests")
class OAuthLoginUseCaseTest {

    @Mock
    private OAuthUserHandlerPort oAuthUserHandler;

    @Mock
    private TokenProviderPort tokenProvider;

    @InjectMocks
    private OAuthLoginUseCase useCase;

    @Test
    @DisplayName("Should login via OAuth successfully and generate token")
    void testExecute() {
        Usuario usuario = Usuario.builder()
                .correoInstitucional("oauth@unas.edu.pe")
                .nombres("OAuth")
                .apellidos("User")
                .rolCodigo("ESTUDIANTE")
                .build();

        when(oAuthUserHandler.findOrCreateFromOAuth("oauth@unas.edu.pe", "OAuth User", "microsoft")).thenReturn(usuario);
        when(tokenProvider.generateToken("oauth@unas.edu.pe", "ESTUDIANTE")).thenReturn("oauth-token");

        LoginResponseDto result = useCase.execute("oauth@unas.edu.pe", "OAuth User", "microsoft");

        assertNotNull(result);
        assertEquals("oauth-token", result.getToken());
        assertEquals("oauth@unas.edu.pe", result.getCorreo());
        assertEquals("ESTUDIANTE", result.getRolCodigo());
        assertFalse(result.isMustChangePassword());

        verify(oAuthUserHandler).findOrCreateFromOAuth("oauth@unas.edu.pe", "OAuth User", "microsoft");
        verify(tokenProvider).generateToken("oauth@unas.edu.pe", "ESTUDIANTE");
    }
}
