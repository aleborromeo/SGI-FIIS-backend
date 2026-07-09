package com.sgi.fiis.auth.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("CustomAuthenticationEntryPoint Unit Tests")
class CustomAuthenticationEntryPointTest {

    private final MessageSource messageSource = Mockito.mock(MessageSource.class);
    private final CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint(messageSource);

    @Test
    @DisplayName("Should write 401 Unauthorized JSON response when authentication fails")
    void testCommence() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/secure");

        MockHttpServletResponse response = new MockHttpServletResponse();
        // AuthenticationException is abstract, so we subclass it anonymously
        AuthenticationException exception = new AuthenticationException("Bad credentials") {};

        when(messageSource.getMessage(eq("auth.unauthorized"), any(), any(), any()))
                .thenReturn("Acceso no autorizado. Debe iniciar sesión e incluir el token JWT en las cabeceras.");

        entryPoint.commence(request, response, exception);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
        assertEquals("UTF-8", response.getCharacterEncoding());

        String content = response.getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("\"status\":401"));
        assertTrue(content.contains("\"error\":\"Unauthorized\""));
        assertTrue(content.contains("\"message\":\"Acceso no autorizado. Debe iniciar sesión e incluir el token JWT en las cabeceras.\""));
        assertTrue(content.contains("\"path\":\"/api/v1/secure\""));
        assertTrue(content.contains("\"timestamp\""));
    }
}
