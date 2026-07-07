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

@ExtendWith(MockitoExtension.class)
class ResolutionsProcedureRepositoryAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

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
        adapter.updateStatusToApprovedWithResolution(1L);

        verify(jdbcTemplate).update(
                "UPDATE tramites SET estado_actual = 'APROBADO_CON_RESOLUCION' WHERE id_tramite = ?",
                1L
        );
    }
}
