package com.sgi.fiis.shared.infrastructure.aspect;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuditingAspect Unit Tests")
class AuditingAspectTest {

    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private JoinPoint joinPoint;
    @Mock private Auditable auditable;
    @InjectMocks private AuditingAspect auditingAspect;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("audit: inserts with authenticated user and request attributes")
    void audit_authenticatedUser() {
        CustomUserDetails userDetails = new CustomUserDetails(10L, "admin", "pass", true,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, null));

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(auditable.action()).thenReturn("CREATE_PROJECT");

        auditingAspect.audit(joinPoint, auditable, new Object());

        verify(jdbcTemplate).update(anyString(), eq("object"), eq(0L), eq("CREAR"),
                eq(10L), eq("192.168.1.1"), any());
    }

    @Test
    @DisplayName("audit: uses X-Forwarded-For header when present")
    void audit_withForwardedFor() {
        SecurityContextHolder.clearContext();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1, 70.41.3.18");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, null));

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(auditable.action()).thenReturn("UPDATE");

        auditingAspect.audit(joinPoint, auditable, null);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(0L), eq("EDITAR"),
                eq(1L), eq("203.0.113.1"), any());
    }

    @Test
    @DisplayName("audit: defaults to 0.0.0.0 when no request attributes")
    void audit_noRequestAttributes() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(auditable.action()).thenReturn("DELETE_PROJECT");

        auditingAspect.audit(joinPoint, auditable, null);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(0L), eq("ELIMINAR"),
                eq(1L), eq("0.0.0.0"), any());
    }

    @Test
    @DisplayName("audit: extracts id from result with getId method")
    void audit_extractsIdFromResult() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(auditable.action()).thenReturn("CREATE");

        ProjectResponse result = ProjectResponse.builder().id(42).build();

        auditingAspect.audit(joinPoint, auditable, result);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(42L), eq("CREAR"),
                eq(1L), eq("0.0.0.0"), any());
    }

    @Test
    @DisplayName("audit: handles null result")
    void audit_nullResult() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(auditable.action()).thenReturn("UPDATE");

        auditingAspect.audit(joinPoint, auditable, null);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(0L), eq("EDITAR"),
                eq(1L), eq("0.0.0.0"), any());
    }

    @Test
    @DisplayName("audit: strips 'interactor' from class name")
    void audit_stripsInteractor() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getTarget()).thenReturn(new TestInteractor());
        when(auditable.action()).thenReturn("CREATE");

        auditingAspect.audit(joinPoint, auditable, null);

        verify(jdbcTemplate).update(anyString(), eq("test"), eq(0L), eq("CREAR"),
                eq(1L), eq("0.0.0.0"), any());
    }

    @Test
    @DisplayName("audit: strips 'service' from class name")
    void audit_stripsService() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getTarget()).thenReturn(new TestService());
        when(auditable.action()).thenReturn("UPDATE");

        auditingAspect.audit(joinPoint, auditable, null);

        verify(jdbcTemplate).update(anyString(), eq("test"), eq(0L), eq("EDITAR"),
                eq(1L), eq("0.0.0.0"), any());
    }

    @Test
    @DisplayName("mapAction: maps all action keywords")
    void mapAction_allMappings() {
        assertEquals("CREAR", callMapAction("CREATE_PROJECT"));
        assertEquals("CREAR", callMapAction("REGISTRAR_USUARIO"));
        assertEquals("CREAR", callMapAction("POSTULAR_PROYECTO"));
        assertEquals("ELIMINAR", callMapAction("DELETE_DRAFT"));
        assertEquals("ELIMINAR", callMapAction("ELIMINAR_PROCEDIMIENTO"));
        assertEquals("ELIMINAR", callMapAction("REJECT_PROJECT"));
        assertEquals("DESACTIVAR", callMapAction("DEACTIVATE_USER"));
        assertEquals("DESACTIVAR", callMapAction("DESACTIVAR_GRUPO"));
        assertEquals("ACTIVAR", callMapAction("ACTIVATE_USER"));
        assertEquals("ACTIVAR", callMapAction("ACTIVAR_GRUPO"));
        assertEquals("LOGIN", callMapAction("USER_LOGIN"));
        assertEquals("LOGOUT", callMapAction("USER_LOGOUT"));
        assertEquals("EDITAR", callMapAction("UNKNOWN_ACTION"));
    }

    private String callMapAction(String action) {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(auditable.action()).thenReturn(action);

        auditingAspect.audit(joinPoint, auditable, null);

        ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(anyString(), anyString(), anyLong(), actionCaptor.capture(), anyLong(), anyString(), any());
        String mapped = actionCaptor.getValue();
        clearInvocations(jdbcTemplate);
        return mapped;
    }

    @Test
    @DisplayName("extractRegisterId: returns 0 for object without Id getter")
    void extractRegisterId_noIdMethod() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getTarget()).thenReturn(new Object());
        when(auditable.action()).thenReturn("UPDATE");

        auditingAspect.audit(joinPoint, auditable, "simple string");

        verify(jdbcTemplate).update(anyString(), anyString(), eq(0L), anyString(), anyLong(), anyString(), any());
    }

    @Test
    @DisplayName("audit: uses 'general' when tablaAfectada is empty")
    void audit_longClassName() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getTarget()).thenReturn(new Object());
        when(auditable.action()).thenReturn("UPDATE");

        auditingAspect.audit(joinPoint, auditable, null);

        verify(jdbcTemplate).update(anyString(), anyString(), anyLong(), anyString(), anyLong(), anyString(), any());
    }

    private static class TestInteractor {}
    private static class TestService {}
}
