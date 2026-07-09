package com.sgi.fiis.auth.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2FailureHandler Unit Tests")
class OAuth2FailureHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;

    @Mock
    private RedirectStrategy redirectStrategy;

    @InjectMocks
    private OAuth2FailureHandler handler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "frontendUrl", "http://localhost:3000");
        handler.setRedirectStrategy(redirectStrategy);
    }

    @Test
    @DisplayName("Should handle OAuth2 authentication failure and redirect with error message")
    void testOnAuthenticationFailure() throws IOException {
        String errorMessage = "User not registered in the system";
        when(exception.getLocalizedMessage()).thenReturn(errorMessage);
        when(exception.getMessage()).thenReturn(errorMessage);

        handler.onAuthenticationFailure(request, response, exception);

        String expectedRedirectUrl = "http://localhost:3000/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        verify(redirectStrategy).sendRedirect(request, response, expectedRedirectUrl);
    }
}
