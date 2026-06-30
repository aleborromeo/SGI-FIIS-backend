package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.domain.port.OAuthUserHandlerPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.users.domain.model.User;
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
        User user = User.builder()
                .institutionalEmail("oauth@unas.edu.pe")
                .firstNames("OAuth")
                .lastNames("User")
                .roleCode("ESTUDIANTE")
                .build();

        when(oAuthUserHandler.findOrCreateFromOAuth("oauth@unas.edu.pe", "OAuth User", "microsoft")).thenReturn(user);
        when(tokenProvider.generateToken("oauth@unas.edu.pe", "ESTUDIANTE")).thenReturn("oauth-token");

        LoginResponseDto result = useCase.execute("oauth@unas.edu.pe", "OAuth User", "microsoft");

        assertNotNull(result);
        assertEquals("oauth-token", result.getToken());
        assertEquals("oauth@unas.edu.pe", result.getEmail());
        assertEquals("ESTUDIANTE", result.getRoleCode());
        assertFalse(result.isMustChangePassword());

        verify(oAuthUserHandler).findOrCreateFromOAuth("oauth@unas.edu.pe", "OAuth User", "microsoft");
        verify(tokenProvider).generateToken("oauth@unas.edu.pe", "ESTUDIANTE");
    }
}
