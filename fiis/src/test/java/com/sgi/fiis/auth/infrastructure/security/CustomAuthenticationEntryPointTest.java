package com.sgi.fiis.auth.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomAuthenticationEntryPoint Unit Tests")
class CustomAuthenticationEntryPointTest {

    private final CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();

    @Test
    @DisplayName("Should write 401 Unauthorized JSON response when authentication fails")
    void testCommence() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/secure");

        MockHttpServletResponse response = new MockHttpServletResponse();
        // AuthenticationException is abstract, so we subclass it anonymously
        AuthenticationException exception = new AuthenticationException("Bad credentials") {};

        entryPoint.commence(request, response, exception);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertEquals("UTF-8", response.getCharacterEncoding());

        String content = response.getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("\"status\":401"));
        assertTrue(content.contains("\"error\":\"Unauthorized\""));
        assertTrue(content.contains("\"message\":\"No autorizado: Debe autenticarse para acceder a este recurso.\""));
        assertTrue(content.contains("\"path\":\"/api/v1/secure\""));
        assertTrue(content.contains("\"timestamp\""));
    }
}
