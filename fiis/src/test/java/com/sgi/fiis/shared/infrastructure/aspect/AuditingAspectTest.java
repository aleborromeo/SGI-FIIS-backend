package com.sgi.fiis.shared.infrastructure.aspect;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        Object target = new Object();
        lenient().when(joinPoint.getTarget()).thenReturn(target);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should audit with authenticated user and direct IP")
    void auditWithAuthenticatedUserAndDirectIp() {
        Auditable auditable = createAuditable("CREATE_USER");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(42L);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("192.168.1.10");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(
                eq("INSERT INTO auditoria_general (tabla_afectada, id_registro, accion, id_usuario, ip_origen, fecha_accion) VALUES (?, ?, ?, ?, ?, ?)"),
                argsCaptor.capture());
        Object[] args = argsCaptor.getValue();
        assertEquals("object", args[0]);
        assertEquals(0L, args[1]);
        assertEquals("CREAR", args[2]);
        assertEquals(42L, args[3]);
        assertEquals("192.168.1.10", args[4]);
        assertNotNull(args[5]);
    }

    @Test
    @DisplayName("Should use X-Forwarded-For when header is present")
    void auditWithXForwardedForHeader() {
        Auditable auditable = createAuditable("UPDATE_USER");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(42L);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.5, 10.0.0.1");
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), argsCaptor.capture());
        assertEquals("203.0.113.5", argsCaptor.getValue()[4]);
    }

    @Test
    @DisplayName("Should default to SYSTEM ID when no authentication is present")
    void auditWithNoAuthentication() {
        Auditable auditable = createAuditable("DELETE_USER");

        SecurityContextHolder.getContext().setAuthentication(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), argsCaptor.capture());
        assertEquals(1L, argsCaptor.getValue()[3]);
    }

    @Test
    @DisplayName("Should default to SYSTEM ID when authentication is not authenticated")
    void auditWithUnauthenticatedAuthentication() {
        Auditable auditable = createAuditable("VIEW");

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
        assertEquals(1L, argsCaptor.getValue()[3]);
    }

    @Test
    @DisplayName("Should default to 0.0.0.0 when no request attributes")
    void auditWithNoRequestAttributes() {
        Auditable auditable = createAuditable("LOGIN");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(42L);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RequestContextHolder.resetRequestAttributes();

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), argsCaptor.capture());
        assertEquals("0.0.0.0", argsCaptor.getValue()[4]);
    }

    @Test
    @DisplayName("Should fall back to remoteAddr when X-Forwarded-For is empty")
    void auditWithEmptyXForwardedForHeader() {
        Auditable auditable = createAuditable("CREATE");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(42L);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), argsCaptor.capture());
        assertEquals("10.0.0.1", argsCaptor.getValue()[4]);
    }

    @Test
    @DisplayName("Should map various actions correctly")
    void audit_mapAction_variousActions() {
        String[] actions = {"DELETE", "DESACTIVAR", "ACTIVAR", "LOGOUT", "UNKNOWN"};
        String[] expectedMapped = {"ELIMINAR", "DESACTIVAR", "ACTIVAR", "LOGOUT", "EDITAR"};

        for (int i = 0; i < actions.length; i++) {
            Auditable auditable = mock(Auditable.class);
            when(auditable.action()).thenReturn(actions[i]);

            auditingAspect.audit(joinPoint, auditable, new Object());

            verify(jdbcTemplate, atLeastOnce()).update(anyString(), argsCaptor.capture());
            assertEquals(expectedMapped[i], argsCaptor.getValue()[2]);
        }
    }

    @Test
    @DisplayName("Should handle various return types for register ID extraction")
    void audit_extractRegisterId_variousResults() {
        Auditable auditable = mock(Auditable.class);
        when(auditable.action()).thenReturn("UPDATE");

        // 1. Null result
        auditingAspect.audit(joinPoint, auditable, null);
        verify(jdbcTemplate, atLeastOnce()).update(anyString(), argsCaptor.capture());
        assertEquals(0L, argsCaptor.getValue()[1]);

        // 2. Result with String ID (non-Number)
        class StringIdObj {
            public String getId() { return "abc"; }
        }
        auditingAspect.audit(joinPoint, auditable, new StringIdObj());
        verify(jdbcTemplate, atLeastOnce()).update(anyString(), argsCaptor.capture());
        assertEquals(0L, argsCaptor.getValue()[1]);

        // 3. Result with valid Long ID
        class LongIdObj {
            public Long getUserId() { return 55L; }
        }
        auditingAspect.audit(joinPoint, auditable, new LongIdObj());
        verify(jdbcTemplate, atLeastOnce()).update(anyString(), argsCaptor.capture());
        assertEquals(55L, argsCaptor.getValue()[1]);
    }

    @Test
    @DisplayName("Should fallback to general when table name is empty or too long")
    void audit_tablaAfectada_fallbackToGeneral() {
        Auditable auditable = mock(Auditable.class);
        when(auditable.action()).thenReturn("UPDATE");

        JoinPoint mockJoinPoint = mock(JoinPoint.class);
        // A very long class name
        class A12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890 {}
        when(mockJoinPoint.getTarget()).thenReturn(new A12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890());

        auditingAspect.audit(mockJoinPoint, auditable, null);
        verify(jdbcTemplate, atLeastOnce()).update(anyString(), argsCaptor.capture());
        assertEquals("general", argsCaptor.getValue()[0]);
    }

    private Auditable createAuditable(String action) {
        return new Auditable() {
            @Override
            public String action() {
                return action;
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Auditable.class;
            }
        };
    }
}
