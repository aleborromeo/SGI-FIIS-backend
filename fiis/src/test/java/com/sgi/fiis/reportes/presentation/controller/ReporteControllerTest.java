package com.sgi.fiis.reportes.presentation.controller;

import com.sgi.fiis.reportes.application.service.ReporteService;
import com.sgi.fiis.reportes.domain.model.*;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de la capa web para {@link ReporteController}.
 * Usa MockMvc standalone para verificar todos los endpoints REST (RF-94, RF-95).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteController - Pruebas de capa web")
class ReporteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReporteService reporteService;

    @InjectMocks
    private ReporteController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/reportes/proyectos
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/reportes/proyectos → 200 OK con lista de proyectos")
    void reporteProyectos_sinFiltros_retorna200() throws Exception {
        ReporteProyecto p = new ReporteProyecto();
        p.setIdProyecto(1);
        p.setCodigoProyecto("PRY-2024-001");
        p.setTituloProyecto("Sistema de Gestión");
        p.setEstadoProyecto("EN_EJECUCION");
        p.setPresupuesto(BigDecimal.valueOf(15000));

        PaginatedResponse<ReporteProyecto> respuesta =
                new PaginatedResponse<>(List.of(p), 1L, 0, 20);

        when(reporteService.generarReporteProyectos(any())).thenReturn(respuesta);

        mockMvc.perform(get("/api/reportes/proyectos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.data[0].codigoProyecto").value("PRY-2024-001"))
                .andExpect(jsonPath("$.data[0].estadoProyecto").value("EN_EJECUCION"));

        verify(reporteService, times(1)).generarReporteProyectos(any());
    }

    @Test
    @DisplayName("GET /api/reportes/proyectos?estado=APROBADO&page=1&size=5 → 200 OK paginado")
    void reporteProyectos_conFiltrosYPaginacion_retorna200() throws Exception {
        PaginatedResponse<ReporteProyecto> respuesta =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 1, 5);

        when(reporteService.generarReporteProyectos(any())).thenReturn(respuesta);

        mockMvc.perform(get("/api/reportes/proyectos")
                        .param("estado", "APROBADO")
                        .param("page", "1")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.data").isArray());

        verify(reporteService, times(1)).generarReporteProyectos(any());
    }

    @Test
    @DisplayName("GET /api/reportes/proyectos → lista vacía retorna 200 con data=[]")
    void reporteProyectos_sinResultados_retorna200ListaVacia() throws Exception {
        PaginatedResponse<ReporteProyecto> respuesta =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 0, 20);

        when(reporteService.generarReporteProyectos(any())).thenReturn(respuesta);

        mockMvc.perform(get("/api/reportes/proyectos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/reportes/tramites
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/reportes/tramites → 200 OK con lista de trámites")
    void reporteTramites_sinFiltros_retorna200() throws Exception {
        ReporteTramite t = new ReporteTramite();
        t.setIdTramite(5);
        t.setCodigoTramite("TRM-2024-005");
        t.setTipoTramite("PROYECTO");
        t.setEstadoActual("EN_REVISION");

        PaginatedResponse<ReporteTramite> respuesta =
                new PaginatedResponse<>(List.of(t), 1L, 0, 20);

        when(reporteService.generarReporteTramites(any())).thenReturn(respuesta);

        mockMvc.perform(get("/api/reportes/tramites")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].codigoTramite").value("TRM-2024-005"))
                .andExpect(jsonPath("$.data[0].tipoTramite").value("PROYECTO"));

        verify(reporteService, times(1)).generarReporteTramites(any());
    }

    @Test
    @DisplayName("GET /api/reportes/tramites?tipoTramite=PLAN_TESIS → 200 OK filtrado")
    void reporteTramites_conFiltroTipo_retorna200() throws Exception {
        PaginatedResponse<ReporteTramite> respuesta =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 0, 20);

        when(reporteService.generarReporteTramites(any())).thenReturn(respuesta);

        mockMvc.perform(get("/api/reportes/tramites")
                        .param("tipoTramite", "PLAN_TESIS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(reporteService, times(1)).generarReporteTramites(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/reportes/resoluciones
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/reportes/resoluciones → 200 OK con lista de resoluciones")
    void reporteResoluciones_sinFiltros_retorna200() throws Exception {
        ReporteResolucion r = new ReporteResolucion();
        r.setIdResolucion(3);
        r.setNumeroResolucion("RES-001-2024");
        r.setAsunto("Aprobación de proyecto");
        r.setFechaEmision(LocalDate.of(2024, Month.APRIL, 10));

        PaginatedResponse<ReporteResolucion> respuesta =
                new PaginatedResponse<>(List.of(r), 1L, 0, 20);

        when(reporteService.generarReporteResoluciones(any())).thenReturn(respuesta);

        mockMvc.perform(get("/api/reportes/resoluciones")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].numeroResolucion").value("RES-001-2024"));

        verify(reporteService, times(1)).generarReporteResoluciones(any());
    }

    @Test
    @DisplayName("GET /api/reportes/resoluciones?fechaDesde=2024-01-01&fechaHasta=2024-12-31 → 200 OK")
    void reporteResoluciones_conFiltroFechas_retorna200() throws Exception {
        PaginatedResponse<ReporteResolucion> respuesta =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 0, 20);

        when(reporteService.generarReporteResoluciones(any())).thenReturn(respuesta);

        mockMvc.perform(get("/api/reportes/resoluciones")
                        .param("fechaDesde", "2024-01-01")
                        .param("fechaHasta", "2024-12-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/reportes/informes-avance
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/reportes/informes-avance → 200 OK con lista de informes")
    void reporteInformesAvance_sinFiltros_retorna200() throws Exception {
        ReporteInformeAvance ia = new ReporteInformeAvance();
        ia.setIdInforme(2);
        ia.setCodigoProyecto("PRY-2024-001");
        ia.setTipoInforme("PARCIAL");
        ia.setEstadoInforme("APROBADO");
        ia.setPorcentajeAvance(BigDecimal.valueOf(45.5));

        PaginatedResponse<ReporteInformeAvance> respuesta =
                new PaginatedResponse<>(List.of(ia), 1L, 0, 20);

        when(reporteService.generarReporteInformes(any())).thenReturn(respuesta);

        mockMvc.perform(get("/api/reportes/informes-avance")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].tipoInforme").value("PARCIAL"))
                .andExpect(jsonPath("$.data[0].estadoInforme").value("APROBADO"));

        verify(reporteService, times(1)).generarReporteInformes(any());
    }

    @Test
    @DisplayName("GET /api/reportes/informes-avance?estado=PENDIENTE → 200 OK filtrado")
    void reporteInformesAvance_conFiltroEstado_retorna200() throws Exception {
        PaginatedResponse<ReporteInformeAvance> respuesta =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 0, 20);

        when(reporteService.generarReporteInformes(any())).thenReturn(respuesta);

        mockMvc.perform(get("/api/reportes/informes-avance")
                        .param("estado", "PENDIENTE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Todos los endpoints responden con Content-Type application/json")
    void todosLosEndpoints_retornanJsonContentType() throws Exception {
        PaginatedResponse<ReporteProyecto> resp = new PaginatedResponse<>(List.of(), 0L, 0, 20);
        when(reporteService.generarReporteProyectos(any())).thenReturn(resp);

        mockMvc.perform(get("/api/reportes/proyectos"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
