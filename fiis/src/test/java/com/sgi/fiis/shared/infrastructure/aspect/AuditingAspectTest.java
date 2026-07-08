package com.sgi.fiis.shared.infrastructure.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditingAspect Unit Tests")
class AuditingAspectTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private JoinPoint joinPoint;

    @InjectMocks
    private AuditingAspect auditingAspect;

    @Captor
    private ArgumentCaptor<Object[]> argsCaptor;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should audit with authenticated user and direct IP")
    void auditWithAuthenticatedUserAndDirectIp() {
        Auditable auditable = mock(Auditable.class);
        when(auditable.action()).thenReturn("CREATE_USER");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john.doe");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("192.168.1.10");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(
                eq("INSERT INTO auditoria_general (usuario, accion, fecha, ip_origen) VALUES (?, ?, ?, ?)"),
                argsCaptor.capture());
        Object[] args = argsCaptor.getValue();
        assertEquals("john.doe", args[0]);
        assertEquals("CREATE_USER", args[1]);
        assertNotNull(args[2]);
        assertEquals("192.168.1.10", args[3]);
    }

    @Test
    @DisplayName("Should use X-Forwarded-For when header is present")
    void auditWithXForwardedForHeader() {
        Auditable auditable = mock(Auditable.class);
        when(auditable.action()).thenReturn("UPDATE_USER");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.5, 10.0.0.1");
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), argsCaptor.capture());
        assertEquals("203.0.113.5", argsCaptor.getValue()[3]);
    }

    @Test
    @DisplayName("Should default to SYSTEM when no authentication is present")
    void auditWithNoAuthentication() {
        Auditable auditable = mock(Auditable.class);
        when(auditable.action()).thenReturn("DELETE_USER");

        SecurityContextHolder.getContext().setAuthentication(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), argsCaptor.capture());
        assertEquals("SYSTEM", argsCaptor.getValue()[0]);
    }

    @Test
    @DisplayName("Should default to SYSTEM when authentication is not authenticated")
    void auditWithUnauthenticatedAuthentication() {
        Auditable auditable = mock(Auditable.class);
        when(auditable.action()).thenReturn("VIEW");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), argsCaptor.capture());
        assertEquals("SYSTEM", argsCaptor.getValue()[0]);
    }

    @Test
    @DisplayName("Should default to 0.0.0.0 when no request attributes")
    void auditWithNoRequestAttributes() {
        Auditable auditable = mock(Auditable.class);
        when(auditable.action()).thenReturn("LOGIN");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RequestContextHolder.resetRequestAttributes();

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), argsCaptor.capture());
        assertEquals("0.0.0.0", argsCaptor.getValue()[3]);
    }

    @Test
    @DisplayName("Should fall back to remoteAddr when X-Forwarded-For is empty")
    void auditWithEmptyXForwardedForHeader() {
        Auditable auditable = mock(Auditable.class);
        when(auditable.action()).thenReturn("CREATE");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), argsCaptor.capture());
        assertEquals("10.0.0.1", argsCaptor.getValue()[3]);
    }
}
