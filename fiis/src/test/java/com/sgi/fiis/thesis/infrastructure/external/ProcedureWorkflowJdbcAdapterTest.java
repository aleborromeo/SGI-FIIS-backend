package com.sgi.fiis.thesis.infrastructure.external;

import com.sgi.fiis.thesis.domain.ThesisProcedureStatus;
import com.sgi.fiis.thesis.domain.ReviewerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcedureWorkflowJdbcAdapter Unit Tests")
class ProcedureWorkflowJdbcAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ProcedureWorkflowJdbcAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProcedureWorkflowJdbcAdapter(jdbcTemplate);
    }

    @Test
    @DisplayName("obtenerIdTramitePorPlanTesis returns tramite id")
    void obtenerIdTramitePorPlanTesis() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(12)))
                .thenReturn(500);

        Integer result = adapter.obtenerIdTramitePorPlanTesis(12);

        assertEquals(500, result);
    }

    @Test
    @DisplayName("obtenerEstadoTramitePorPlanTesis returns estado")
    void obtenerEstadoTramitePorPlanTesis() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(12)))
                .thenReturn("PENDIENTE_COORDINADOR");

        String result = adapter.obtenerEstadoTramitePorPlanTesis(12);

        assertEquals("PENDIENTE_COORDINADOR", result);
    }

    @Test
    @DisplayName("obtenerRevisorTramitePorPlanTesis returns revisor")
    void obtenerRevisorTramitePorPlanTesis() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(12)))
                .thenReturn("COORDINADOR_GRUPO");

        String result = adapter.obtenerRevisorTramitePorPlanTesis(12);

        assertEquals("COORDINADOR_GRUPO", result);
    }

    @Test
    @DisplayName("findPlanTesisIdsByRevisor returns list of ids")
    void findPlanTesisIdsByRevisor() {
        when(jdbcTemplate.queryForList(anyString(), eq(Integer.class), eq("COORDINADOR_GRUPO")))
                .thenReturn(List.of(12, 15));

        List<Integer> result = adapter.findPlanTesisIdsByRevisor(ReviewerRole.COORDINADOR_GRUPO);

        assertEquals(2, result.size());
        assertTrue(result.contains(12));
        assertTrue(result.contains(15));
    }

    @Test
    @DisplayName("crearTramitePlanTesis inserts and returns id via fallback query")
    void crearTramitePlanTesis() {
        when(jdbcTemplate.queryForObject(contains("SELECT COALESCE(MAX(id_tramite), 0) + 1"), eq(Integer.class)))
                .thenReturn(42);

        when(jdbcTemplate.update(any(org.springframework.jdbc.core.PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenReturn(1);

        when(jdbcTemplate.queryForObject(contains("SELECT id_tramite FROM tramites"), eq(Integer.class), eq(12)))
                .thenReturn(500);

        Integer result = adapter.crearTramitePlanTesis(12, 101L, 2);

        assertEquals(500, result);
    }

    @Test
    @DisplayName("derivarPlanTesis updates tramite and inserts movimiento")
    void derivarPlanTesis() {
        when(jdbcTemplate.queryForObject(contains("SELECT id_tramite FROM tramites"), eq(Integer.class), eq(12)))
                .thenReturn(500);

        when(jdbcTemplate.queryForObject(contains("SELECT estado_actual FROM tramites"), eq(String.class), eq(500)))
                .thenReturn("PENDIENTE_COORDINADOR");

        adapter.derivarPlanTesis(12, 303L, ThesisProcedureStatus.PENDIENTE_DIRECCION,
                ReviewerRole.DIRECTOR_INVESTIGACION, "APROBAR_COORDINADOR", null, null);

        verify(jdbcTemplate, times(1)).update(contains("UPDATE tramites"), anyString(), anyString(), anyInt());
        verify(jdbcTemplate, times(1)).update(contains("INSERT INTO movimientos_tramite"), anyInt(), anyLong(),
                anyString(), anyString(), anyString(), isNull(), isNull());
    }

    @Test
    @DisplayName("registrarResolucion inserts resolution and updates tramite")
    void registrarResolucion() {
        when(jdbcTemplate.queryForObject(contains("SELECT id_tramite FROM tramites"), eq(Integer.class), eq(12)))
                .thenReturn(500);

        when(jdbcTemplate.queryForObject(contains("SELECT estado_actual FROM tramites"), eq(String.class), eq(500)))
                .thenReturn("PENDIENTE_DECANATO");

        when(jdbcTemplate.update(any(org.springframework.jdbc.core.PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenReturn(1);

        Integer result = adapter.registrarResolucion(12, 505L, "RES-001",
                LocalDate.of(2026, 7, 13), "Aprobado", 200);

        assertNull(result);
    }
}
