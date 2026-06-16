package com.sgi.fiis.reportes.presentation.controller;
import com.sgi.fiis.reportes.application.service.TrazabilidadService;
import com.sgi.fiis.reportes.domain.model.TrazabilidadMovimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de la capa web para {@link TrazabilidadController}.
 * Usa MockMvc en modo standalone (sin contexto Spring completo),
 * compatible con Spring Boot 4.x / Spring Framework 7.x.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TrazabilidadController - Pruebas de capa web")
class TrazabilidadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TrazabilidadService trazabilidadService;

    @InjectMocks
    private TrazabilidadController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/reportes/trazabilidad/{idTramite} — respuesta exitosa
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/reportes/trazabilidad/10 → 200 OK con historial")
    void getTrazabilidad_conMovimientos_retorna200() throws Exception {
        // arrange
        TrazabilidadMovimiento mov = new TrazabilidadMovimiento();
        mov.setIdMovimiento(1);
        mov.setIdTramite(10);
        mov.setCodigoTramite("TRM-2024-001");
        mov.setNombreUsuarioAccion("Juan Pérez");
        mov.setAccion("CREACION");
        mov.setEstadoNuevo("PENDIENTE");
        mov.setObservacion("Trámite creado");
        mov.setFechaMovimiento(LocalDateTime.of(2024, 1, 15, 9, 0));

        when(trazabilidadService.consultarTrazabilidad(10)).thenReturn(List.of(mov));

        // act & assert
        mockMvc.perform(get("/api/reportes/trazabilidad/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idTramite").value(10))
                .andExpect(jsonPath("$[0].codigoTramite").value("TRM-2024-001"))
                .andExpect(jsonPath("$[0].accion").value("CREACION"))
                .andExpect(jsonPath("$[0].estadoNuevo").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].nombreUsuarioAccion").value("Juan Pérez"));

        verify(trazabilidadService, times(1)).consultarTrazabilidad(10);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Trámite sin movimientos → lista vacía
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/reportes/trazabilidad/999 → 200 OK con lista vacía")
    void getTrazabilidad_sinMovimientos_retorna200ConListaVacia() throws Exception {
        // arrange
        when(trazabilidadService.consultarTrazabilidad(999)).thenReturn(Collections.emptyList());

        // act & assert
        mockMvc.perform(get("/api/reportes/trazabilidad/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(trazabilidadService, times(1)).consultarTrazabilidad(999);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Múltiples movimientos — verifica orden y campos del segundo
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/reportes/trazabilidad/10 → retorna múltiples movimientos en orden")
    void getTrazabilidad_multiplesMovimientos_retornaOrdenCorrecto() throws Exception {
        // arrange
        TrazabilidadMovimiento mov1 = new TrazabilidadMovimiento();
        mov1.setIdMovimiento(1);
        mov1.setIdTramite(10);
        mov1.setAccion("CREACION");
        mov1.setEstadoNuevo("PENDIENTE");
        mov1.setFechaMovimiento(LocalDateTime.of(2024, 1, 15, 9, 0));

        TrazabilidadMovimiento mov2 = new TrazabilidadMovimiento();
        mov2.setIdMovimiento(2);
        mov2.setIdTramite(10);
        mov2.setAccion("APROBACION");
        mov2.setEstadoAnterior("PENDIENTE");
        mov2.setEstadoNuevo("APROBADO");
        mov2.setFechaMovimiento(LocalDateTime.of(2024, 1, 16, 14, 30));

        when(trazabilidadService.consultarTrazabilidad(10)).thenReturn(List.of(mov1, mov2));

        // act & assert
        mockMvc.perform(get("/api/reportes/trazabilidad/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accion").value("CREACION"))
                .andExpect(jsonPath("$[1].accion").value("APROBACION"))
                .andExpect(jsonPath("$[1].estadoAnterior").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].estadoNuevo").value("APROBADO"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Verifica que el Content-Type de la respuesta es application/json
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Debe responder con Content-Type application/json")
    void getTrazabilidad_contentTypeEsJson() throws Exception {
        // arrange
        when(trazabilidadService.consultarTrazabilidad(anyInt())).thenReturn(Collections.emptyList());

        // act & assert
        mockMvc.perform(get("/api/reportes/trazabilidad/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
