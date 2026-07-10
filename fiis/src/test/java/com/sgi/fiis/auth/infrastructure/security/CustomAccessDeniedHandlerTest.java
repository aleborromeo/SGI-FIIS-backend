package com.sgi.fiis.auth.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CustomAccessDeniedHandler Unit Tests")
class CustomAccessDeniedHandlerTest {

    private final MessageSource messageSource = mock(MessageSource.class);
    private final CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler(messageSource);

    @Test
    @DisplayName("Should write 403 Forbidden JSON response when access is denied")
    void testHandle() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");

        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException exception = new AccessDeniedException("Access is denied");

        when(messageSource.getMessage(eq("auth.forbidden"), any(), any(), any()))
                .thenReturn("Acceso denegado. No tiene los privilegios necesarios para acceder a este recurso.");

        handler.handle(request, response, exception);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
        assertEquals("UTF-8", response.getCharacterEncoding());

        String content = response.getContentAsString();
        assertNotNull(content);
        assertTrue(content.contains("\"status\":403"));
        assertTrue(content.contains("\"error\":\"Forbidden\""));
        assertTrue(content.contains("\"message\":\"Acceso denegado. No tiene los privilegios necesarios para acceder a este recurso.\""));
        assertTrue(content.contains("\"path\":\"/api/v1/test\""));
        assertTrue(content.contains("\"timestamp\""));
    }
}
