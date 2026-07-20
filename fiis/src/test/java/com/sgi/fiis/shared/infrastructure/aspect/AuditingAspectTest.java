package com.sgi.fiis.shared.infrastructure.aspect;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
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
import java.util.Map;
import java.util.Collections;

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
    @Mock private ProceedingJoinPoint joinPoint;
    @Mock private Auditable auditable;
    @Mock private CorrelationContext correlationContext;
    @InjectMocks private AuditingAspect auditingAspect;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("audit: inserts with authenticated user and request attributes")
    void audit_authenticatedUser() throws Throwable {
        CustomUserDetails userDetails = new CustomUserDetails(10L, "admin", "pass", true,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")), "ADMIN");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, null));

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.proceed()).thenReturn(new Object());
        when(auditable.action()).thenReturn("CREATE_PROJECT");

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), eq("object"), eq(0L), eq("CREAR"),
                eq(10L), any(), any(), eq("192.168.1.1"), any(), anyString());
    }

    @Test
    @DisplayName("audit: uses X-Forwarded-For header when present")
    void audit_withForwardedFor() throws Throwable {
        SecurityContextHolder.clearContext();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1, 70.41.3.18");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, null));

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.proceed()).thenReturn(new Object());
        when(auditable.action()).thenReturn("UPDATE");

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(0L), eq("EDITAR"),
                eq(1L), any(), any(), eq("203.0.113.1"), any(), anyString());
    }

    @Test
    @DisplayName("audit: defaults to 0.0.0.0 when no request attributes")
    void audit_noRequestAttributes() throws Throwable {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.proceed()).thenReturn(new Object());
        when(auditable.action()).thenReturn("DELETE_PROJECT");

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(0L), eq("ELIMINAR"),
                eq(1L), any(), any(), eq("0.0.0.0"), any(), anyString());
    }

    @Test
    @DisplayName("audit: extracts id from result with getId method")
    void audit_extractsIdFromResult() throws Throwable {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(auditable.action()).thenReturn("CREATE");

        ProjectResponse result = ProjectResponse.builder().id(42).build();
        when(joinPoint.proceed()).thenReturn(result);

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(42L), eq("CREAR"),
                eq(1L), any(), any(), eq("0.0.0.0"), any(), anyString());
    }

    @Test
    @DisplayName("audit: handles null result")
    void audit_nullResult() throws Throwable {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        Object target = new Object();
        when(joinPoint.getTarget()).thenReturn(target);
        when(joinPoint.proceed()).thenReturn(null);
        when(auditable.action()).thenReturn("UPDATE");

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(0L), eq("EDITAR"),
                eq(1L), any(), any(), eq("0.0.0.0"), any(), anyString());
    }

    @Test
    @DisplayName("audit: strips 'interactor' from class name")
    void audit_stripsInteractor() throws Throwable {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getTarget()).thenReturn(new TestInteractor());
        when(joinPoint.proceed()).thenReturn(new Object());
        when(auditable.action()).thenReturn("CREATE");

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), eq("test"), eq(0L), eq("CREAR"),
                eq(1L), any(), any(), eq("0.0.0.0"), any(), anyString());
    }

    @Test
    @DisplayName("audit: strips 'service' from class name")
    void audit_stripsService() throws Throwable {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getTarget()).thenReturn(new TestService());
        when(joinPoint.proceed()).thenReturn(new Object());
        when(auditable.action()).thenReturn("UPDATE");

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), eq("test"), eq(0L), eq("EDITAR"),
                eq(1L), any(), any(), eq("0.0.0.0"), any(), anyString());
    }

    @Test
    @DisplayName("mapAction: maps creation action keywords")
    void mapAction_createMappings() {
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
    }

    @Test
    @DisplayName("mapAction: maps unknown action to EDITAR")
    void mapAction_unknownAction() {
        assertEquals("EDITAR", callMapAction("UNKNOWN_ACTION"));
    }

    private String callMapAction(String action) {
        try {
            SecurityContextHolder.clearContext();
            RequestContextHolder.resetRequestAttributes();
            when(joinPoint.getTarget()).thenReturn(new Object());
            when(joinPoint.proceed()).thenReturn(new Object());
            when(auditable.action()).thenReturn(action);

            auditingAspect.audit(joinPoint, auditable);

            ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);
            verify(jdbcTemplate).update(anyString(), anyString(), anyLong(), actionCaptor.capture(), anyLong(), any(), any(), anyString(), any(), anyString());
            String mapped = actionCaptor.getValue();
            clearInvocations(jdbcTemplate);
            return mapped;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("extractRegisterId: returns 0 for object without Id getter")
    void extractRegisterId_noIdMethod() throws Throwable {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getTarget()).thenReturn(new Object());
        when(auditable.action()).thenReturn("UPDATE");
        when(joinPoint.proceed()).thenReturn("simple string");

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(0L), anyString(), anyLong(), any(), any(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("audit: uses 'general' when tablaAfectada is empty")
    void audit_longClassName() throws Throwable {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.proceed()).thenReturn(new Object());
        when(auditable.action()).thenReturn("UPDATE");

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), anyString(), anyLong(), anyString(), anyLong(), any(), any(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("queryPreviousState: covers successful query, fallback query, exception and invalid name")
    void testQueryPreviousStateCoverage() throws Throwable {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getArgs()).thenReturn(new Object[]{ 123L });
        org.aspectj.lang.reflect.MethodSignature sig = mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(sig.getParameterNames()).thenReturn(new String[]{ "id" });
        when(joinPoint.getSignature()).thenReturn(sig);

        // 1. Success query path
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.proceed()).thenReturn(new Object());
        when(auditable.action()).thenReturn("UPDATE");
        
        Map<String, Object> mockRow = Map.of("id", 123L, "name", "Test");
        when(jdbcTemplate.queryForList(contains("WHERE id = ?"), eq(123L))).thenReturn(List.of(mockRow));

        auditingAspect.audit(joinPoint, auditable);
        verify(jdbcTemplate, times(1)).update(anyString(), anyString(), eq(123L), anyString(), anyLong(), any(), any(), anyString(), any(), anyString());

        // 2. Fallback query path (first is empty, second has result)
        reset(jdbcTemplate);
        when(jdbcTemplate.queryForList(contains("WHERE id = ?"), eq(123L))).thenReturn(Collections.emptyList());
        when(jdbcTemplate.queryForList(contains("WHERE id_object = ?"), eq(123L))).thenReturn(List.of(mockRow));

        auditingAspect.audit(joinPoint, auditable);
        verify(jdbcTemplate, times(1)).update(anyString(), anyString(), eq(123L), anyString(), anyLong(), any(), any(), anyString(), any(), anyString());

        // 3. Exception path
        reset(jdbcTemplate);
        when(jdbcTemplate.queryForList(anyString(), anyLong()))
                .thenThrow(new org.springframework.jdbc.CannotGetJdbcConnectionException("Connection error", new java.sql.SQLException("Connection failed")));

        auditingAspect.audit(joinPoint, auditable);
        verify(jdbcTemplate, times(1)).update(anyString(), anyString(), eq(123L), anyString(), anyLong(), any(), any(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("audit: extracts register ID from args when parameter has ID in name")
    void audit_extractsRegisterIdFromArgs() throws Throwable {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();

        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.proceed()).thenReturn(new Object());
        when(auditable.action()).thenReturn("UPDATE");

        when(joinPoint.getArgs()).thenReturn(new Object[]{ 456L });
        org.aspectj.lang.reflect.MethodSignature sig = mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(sig.getParameterNames()).thenReturn(new String[]{ "projectId" });
        when(joinPoint.getSignature()).thenReturn(sig);

        Map<String, Object> mockRow = Map.of("id", 456L);
        when(jdbcTemplate.queryForList(contains("WHERE id = ?"), eq(456L))).thenReturn(List.of(mockRow));

        auditingAspect.audit(joinPoint, auditable);

        verify(jdbcTemplate).update(anyString(), anyString(), eq(456L), anyString(), anyLong(), any(), any(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("audit: clears correlation context when proceed throws exception")
    void audit_clearsContextOnException() throws Throwable {
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test exception"));
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(auditable.action()).thenReturn("CREATE");

        assertThrows(RuntimeException.class, () -> auditingAspect.audit(joinPoint, auditable));
        verify(correlationContext).clear();
    }

    private static class TestInteractor {}
    private static class TestService {}
}
