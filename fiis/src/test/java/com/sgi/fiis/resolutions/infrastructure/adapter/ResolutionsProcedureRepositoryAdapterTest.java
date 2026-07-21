package com.sgi.fiis.resolutions.infrastructure.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class ResolutionsProcedureRepositoryAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private com.sgi.fiis.tramites.application.usecase.RegisterResolutionUseCase registerResolutionUseCase;

    @InjectMocks
    private ResolutionsProcedureRepositoryAdapter adapter;

    @Test
    void existsProcedure_shouldReturnTrue_whenCountIsGreaterThanZero() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1L)))
                .thenReturn(1);

        assertTrue(adapter.existsProcedure(1L));
    }

    @Test
    void existsProcedure_shouldReturnFalse_whenCountIsZero() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1L)))
                .thenReturn(0);

        assertFalse(adapter.existsProcedure(1L));
    }

    @Test
    void existsProcedure_shouldReturnFalse_whenCountIsNull() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1L)))
                .thenReturn(null);

        assertFalse(adapter.existsProcedure(1L));
    }

    @Test
    void updateStatusToApprovedWithResolution_shouldExecuteUpdate() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        adapter.updateStatusToApprovedWithResolution(1L);

        verify(registerResolutionUseCase).execute(1L, 1L);
    }

    @Test
    void updateStatusToApprovedWithResolution_withSecurityContext_shouldUseUserId() {
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        com.sgi.fiis.auth.infrastructure.security.CustomUserDetails userDetails = mock(com.sgi.fiis.auth.infrastructure.security.CustomUserDetails.class);
        
        when(userDetails.getId()).thenReturn(99L);
        when(auth.getPrincipal()).thenReturn(userDetails);
        
        org.springframework.security.core.context.SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
        
        try {
            adapter.updateStatusToApprovedWithResolution(1L);
            verify(registerResolutionUseCase).execute(1L, 99L);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    @Test
    void updateStatusToApprovedWithResolution_withAnonymousSecurityContext_shouldUseFallbackId() {
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getPrincipal()).thenReturn("anonymousUser");
        
        org.springframework.security.core.context.SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
        
        try {
            adapter.updateStatusToApprovedWithResolution(1L);
            verify(registerResolutionUseCase).execute(1L, 1L);
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }
}
