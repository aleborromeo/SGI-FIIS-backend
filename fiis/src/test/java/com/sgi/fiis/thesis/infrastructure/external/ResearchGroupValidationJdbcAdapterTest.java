package com.sgi.fiis.thesis.infrastructure.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResearchGroupValidationJdbcAdapter Unit Tests")
class ResearchGroupValidationJdbcAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ResearchGroupValidationJdbcAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ResearchGroupValidationJdbcAdapter(jdbcTemplate);
    }

    @Test
    @DisplayName("existeGrupoActivo returns true when count > 0")
    void existeGrupoActivoReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);

        assertTrue(adapter.existeGrupoActivo(1));
    }

    @Test
    @DisplayName("existeGrupoActivo returns false when count is 0")
    void existeGrupoActivoReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(99)))
                .thenReturn(0);

        assertFalse(adapter.existeGrupoActivo(99));
    }

    @Test
    @DisplayName("existeLineaActiva returns true when count > 0")
    void existeLineaActivaReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(5)))
                .thenReturn(1);

        assertTrue(adapter.existeLineaActiva(5));
    }

    @Test
    @DisplayName("existeLineaActiva returns false when count is 0")
    void existeLineaActivaReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(99)))
                .thenReturn(0);

        assertFalse(adapter.existeLineaActiva(99));
    }

    @Test
    @DisplayName("lineaPerteneceAlGrupo returns true when count > 0")
    void lineaPerteneceAlGrupoReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(2), eq(5)))
                .thenReturn(1);

        assertTrue(adapter.lineaPerteneceAlGrupo(2, 5));
    }

    @Test
    @DisplayName("lineaPerteneceAlGrupo returns false when count is 0")
    void lineaPerteneceAlGrupoReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(2), eq(99)))
                .thenReturn(0);

        assertFalse(adapter.lineaPerteneceAlGrupo(2, 99));
    }

    @Test
    @DisplayName("esCoordinadorDelGrupo returns true when count > 0")
    void esCoordinadorDelGrupoReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(303L), eq(2)))
                .thenReturn(1);

        assertTrue(adapter.esCoordinadorDelGrupo(303L, 2));
    }

    @Test
    @DisplayName("esCoordinadorDelGrupo returns false when count is 0")
    void esCoordinadorDelGrupoReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(303L), eq(99)))
                .thenReturn(0);

        assertFalse(adapter.esCoordinadorDelGrupo(303L, 99));
    }
}
