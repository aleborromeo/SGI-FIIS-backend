package com.sgi.fiis.dashboards.presentation.controller;

import com.sgi.fiis.dashboards.application.dto.AlertaItemResponse;
import com.sgi.fiis.dashboards.application.dto.DashboardAdminResponse;
import com.sgi.fiis.dashboards.application.dto.DashboardCoordinadorResponse;
import com.sgi.fiis.dashboards.application.dto.DashboardDecanoResponse;
import com.sgi.fiis.dashboards.application.dto.DashboardDirectorResponse;
import com.sgi.fiis.dashboards.application.dto.DashboardDocenteResponse;
import com.sgi.fiis.dashboards.application.dto.DashboardEstudianteResponse;
import com.sgi.fiis.dashboards.application.dto.DashboardEvaluadorResponse;
import com.sgi.fiis.dashboards.application.usecase.ObtenerDashboardAdminUseCase;
import com.sgi.fiis.dashboards.application.usecase.ObtenerDashboardCoordinadorUseCase;
import com.sgi.fiis.dashboards.application.usecase.ObtenerDashboardDecanoUseCase;
import com.sgi.fiis.dashboards.application.usecase.ObtenerDashboardDirectorUseCase;
import com.sgi.fiis.dashboards.application.usecase.ObtenerDashboardDocenteUseCase;
import com.sgi.fiis.dashboards.application.usecase.ObtenerDashboardEstudianteUseCase;
import com.sgi.fiis.dashboards.application.usecase.ObtenerDashboardEvaluadorUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardController Unit Tests")
class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ObtenerDashboardAdminUseCase dashboardAdminUseCase;

    @Mock
    private ObtenerDashboardDirectorUseCase dashboardDirectorUseCase;

    @Mock
    private ObtenerDashboardCoordinadorUseCase dashboardCoordinadorUseCase;

    @Mock
    private ObtenerDashboardDocenteUseCase dashboardDocenteUseCase;

    @Mock
    private ObtenerDashboardEvaluadorUseCase dashboardEvaluadorUseCase;

    @Mock
    private ObtenerDashboardDecanoUseCase dashboardDecanoUseCase;

    @Mock
    private ObtenerDashboardEstudianteUseCase dashboardEstudianteUseCase;

    @BeforeEach
    void setUp() {
        DashboardController controller = new DashboardController(
                dashboardAdminUseCase,
                dashboardDirectorUseCase,
                dashboardCoordinadorUseCase,
                dashboardDocenteUseCase,
                dashboardEvaluadorUseCase,
                dashboardDecanoUseCase,
                dashboardEstudianteUseCase
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("GET /api/dashboard/admin/{idUsuario} debe retornar dashboard admin")
    void getDashboardAdmin_debeRetornarOk() throws Exception {
        DashboardAdminResponse response = DashboardAdminResponse.builder()
                .totalUsuarios(10)
                .totalUsuariosActivos(8)
                .totalGrupos(3)
                .totalGruposActivos(2)
                .totalProyectos(5)
                .proyectosActivos(4)
                .tramitesPendientes(6)
                .resolucionesEmitidas(7)
                .tramitesEnRevision(1)
                .tramitesAprobados(2)
                .tramitesRechazados(3)
                .alertas(List.of(
                        AlertaItemResponse.builder()
                                .tipo("REVISION")
                                .titulo("Trámites pendientes")
                                .descripcion("Hay trámites pendientes")
                                .build()
                ))
                .build();

        when(dashboardAdminUseCase.ejecutar(1)).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/admin/{idUsuario}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").value(10))
                .andExpect(jsonPath("$.totalUsuariosActivos").value(8))
                .andExpect(jsonPath("$.totalGrupos").value(3))
                .andExpect(jsonPath("$.totalGruposActivos").value(2))
                .andExpect(jsonPath("$.totalProyectos").value(5))
                .andExpect(jsonPath("$.proyectosActivos").value(4))
                .andExpect(jsonPath("$.tramitesPendientes").value(6))
                .andExpect(jsonPath("$.resolucionesEmitidas").value(7))
                .andExpect(jsonPath("$.tramitesEnRevision").value(1))
                .andExpect(jsonPath("$.tramitesAprobados").value(2))
                .andExpect(jsonPath("$.tramitesRechazados").value(3))
                .andExpect(jsonPath("$.alertas", hasSize(1)))
                .andExpect(jsonPath("$.alertas[0].tipo").value("REVISION"))
                .andExpect(jsonPath("$.alertas[0].titulo").value("Trámites pendientes"))
                .andExpect(jsonPath("$.alertas[0].descripcion").value("Hay trámites pendientes"));
    }

    @Test
    @DisplayName("GET /api/dashboard/director/{idUsuario} debe retornar dashboard director")
    void getDashboardDirector_debeRetornarOk() throws Exception {
        DashboardDirectorResponse response = DashboardDirectorResponse.builder()
                .totalProyectos(20)
                .proyectosActivos(12)
                .proyectosPostulados(5)
                .proyectosObservados(2)
                .tramitesPendientesRevision(4)
                .informesPorVencer(3)
                .resolucionesEmitidas(9)
                .convocatoriasAbiertas(1)
                .tramitesEnCoordinador(2)
                .tramitesEnDirector(3)
                .tramitesEnDecano(4)
                .tramitesFinalizados(5)
                .alertas(List.of())
                .build();

        when(dashboardDirectorUseCase.ejecutar(2)).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/director/{idUsuario}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProyectos").value(20))
                .andExpect(jsonPath("$.proyectosActivos").value(12))
                .andExpect(jsonPath("$.proyectosPostulados").value(5))
                .andExpect(jsonPath("$.proyectosObservados").value(2))
                .andExpect(jsonPath("$.tramitesPendientesRevision").value(4))
                .andExpect(jsonPath("$.informesPorVencer").value(3))
                .andExpect(jsonPath("$.resolucionesEmitidas").value(9))
                .andExpect(jsonPath("$.convocatoriasAbiertas").value(1))
                .andExpect(jsonPath("$.tramitesEnCoordinador").value(2))
                .andExpect(jsonPath("$.tramitesEnDirector").value(3))
                .andExpect(jsonPath("$.tramitesEnDecano").value(4))
                .andExpect(jsonPath("$.tramitesFinalizados").value(5))
                .andExpect(jsonPath("$.alertas", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/coordinador/{idUsuario} debe retornar dashboard coordinador")
    void getDashboardCoordinador_debeRetornarOk() throws Exception {
        DashboardCoordinadorResponse response = DashboardCoordinadorResponse.builder()
                .idGrupo(3)
                .nombreGrupo("Grupo FIIS")
                .codigoGrupo("GI-FIIS")
                .totalMiembros(8)
                .miembrosActivos(7)
                .totalProyectosGrupo(4)
                .proyectosActivosGrupo(3)
                .tramitesPendientesGrupo(2)
                .informesAvanceGrupo(5)
                .planesTesisGrupo(6)
                .tramitesPostulados(1)
                .tramitesEnRevision(2)
                .tramitesAprobados(3)
                .tramitesObservados(4)
                .alertas(List.of())
                .build();

        when(dashboardCoordinadorUseCase.ejecutar(3)).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/coordinador/{idUsuario}", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idGrupo").value(3))
                .andExpect(jsonPath("$.nombreGrupo").value("Grupo FIIS"))
                .andExpect(jsonPath("$.codigoGrupo").value("GI-FIIS"))
                .andExpect(jsonPath("$.totalMiembros").value(8))
                .andExpect(jsonPath("$.miembrosActivos").value(7))
                .andExpect(jsonPath("$.totalProyectosGrupo").value(4))
                .andExpect(jsonPath("$.proyectosActivosGrupo").value(3))
                .andExpect(jsonPath("$.tramitesPendientesGrupo").value(2))
                .andExpect(jsonPath("$.informesAvanceGrupo").value(5))
                .andExpect(jsonPath("$.planesTesisGrupo").value(6))
                .andExpect(jsonPath("$.tramitesPostulados").value(1))
                .andExpect(jsonPath("$.tramitesEnRevision").value(2))
                .andExpect(jsonPath("$.tramitesAprobados").value(3))
                .andExpect(jsonPath("$.tramitesObservados").value(4))
                .andExpect(jsonPath("$.alertas", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/docente/{idUsuario} debe retornar dashboard docente")
    void getDashboardDocente_debeRetornarOk() throws Exception {
        DashboardDocenteResponse response = DashboardDocenteResponse.builder()
                .proyectosComoResponsable(2)
                .proyectosComoIntegrante(4)
                .tramitesPendientes(1)
                .informesAvancePendientes(3)
                .documentosCargados(6)
                .resolucionesRecibidas(5)
                .proyectosPostulados(1)
                .proyectosAprobados(2)
                .proyectosEnEjecucion(3)
                .proyectosFinalizados(4)
                .alertas(List.of())
                .build();

        when(dashboardDocenteUseCase.ejecutar(4)).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/docente/{idUsuario}", 4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proyectosComoResponsable").value(2))
                .andExpect(jsonPath("$.proyectosComoIntegrante").value(4))
                .andExpect(jsonPath("$.tramitesPendientes").value(1))
                .andExpect(jsonPath("$.informesAvancePendientes").value(3))
                .andExpect(jsonPath("$.documentosCargados").value(6))
                .andExpect(jsonPath("$.resolucionesRecibidas").value(5))
                .andExpect(jsonPath("$.proyectosPostulados").value(1))
                .andExpect(jsonPath("$.proyectosAprobados").value(2))
                .andExpect(jsonPath("$.proyectosEnEjecucion").value(3))
                .andExpect(jsonPath("$.proyectosFinalizados").value(4))
                .andExpect(jsonPath("$.alertas", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/evaluador/{idUsuario} debe retornar dashboard evaluador")
    void getDashboardEvaluador_debeRetornarOk() throws Exception {
        DashboardEvaluadorResponse response = DashboardEvaluadorResponse.builder()
                .evaluacionesAsignadas(10)
                .evaluacionesPendientes(3)
                .evaluacionesCompletadas(7)
                .proyectosAsignados(4)
                .planesTesisAsignados(5)
                .evaluacionesAprobadas(2)
                .evaluacionesRechazadas(1)
                .evaluacionesConObservaciones(6)
                .alertas(List.of())
                .build();

        when(dashboardEvaluadorUseCase.ejecutar(5)).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/evaluador/{idUsuario}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluacionesAsignadas").value(10))
                .andExpect(jsonPath("$.evaluacionesPendientes").value(3))
                .andExpect(jsonPath("$.evaluacionesCompletadas").value(7))
                .andExpect(jsonPath("$.proyectosAsignados").value(4))
                .andExpect(jsonPath("$.planesTesisAsignados").value(5))
                .andExpect(jsonPath("$.evaluacionesAprobadas").value(2))
                .andExpect(jsonPath("$.evaluacionesRechazadas").value(1))
                .andExpect(jsonPath("$.evaluacionesConObservaciones").value(6))
                .andExpect(jsonPath("$.alertas", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/decano/{idUsuario} debe retornar dashboard decano")
    void getDashboardDecano_debeRetornarOk() throws Exception {
        DashboardDecanoResponse response = DashboardDecanoResponse.builder()
                .totalProyectosFacultad(30)
                .proyectosActivos(15)
                .tramitesPendientesFirma(4)
                .resolucionesEmitidas(8)
                .convocatoriasActivas(2)
                .totalGruposActivos(6)
                .tramitesEnEspera(3)
                .tramitesAprobadosMes(5)
                .tramitesRechazadosMes(1)
                .alertas(List.of())
                .build();

        when(dashboardDecanoUseCase.ejecutar(6)).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/decano/{idUsuario}", 6))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProyectosFacultad").value(30))
                .andExpect(jsonPath("$.proyectosActivos").value(15))
                .andExpect(jsonPath("$.tramitesPendientesFirma").value(4))
                .andExpect(jsonPath("$.resolucionesEmitidas").value(8))
                .andExpect(jsonPath("$.convocatoriasActivas").value(2))
                .andExpect(jsonPath("$.totalGruposActivos").value(6))
                .andExpect(jsonPath("$.tramitesEnEspera").value(3))
                .andExpect(jsonPath("$.tramitesAprobadosMes").value(5))
                .andExpect(jsonPath("$.tramitesRechazadosMes").value(1))
                .andExpect(jsonPath("$.alertas", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/estudiante/{idUsuario} debe retornar dashboard estudiante")
    void getDashboardEstudiante_debeRetornarOk() throws Exception {
        DashboardEstudianteResponse response = DashboardEstudianteResponse.builder()
                .planesTesisPresentados(1)
                .estadoPlanActual("OBSERVADO")
                .tramitesPendientes(2)
                .documentosCargados(3)
                .convocatoriasAbiertas(4)
                .nombreGrupo("Grupo Tesis")
                .codigoGrupo("GT-01")
                .alertas(List.of())
                .build();

        when(dashboardEstudianteUseCase.ejecutar(7)).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/estudiante/{idUsuario}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planesTesisPresentados").value(1))
                .andExpect(jsonPath("$.estadoPlanActual").value("OBSERVADO"))
                .andExpect(jsonPath("$.tramitesPendientes").value(2))
                .andExpect(jsonPath("$.documentosCargados").value(3))
                .andExpect(jsonPath("$.convocatoriasAbiertas").value(4))
                .andExpect(jsonPath("$.nombreGrupo").value("Grupo Tesis"))
                .andExpect(jsonPath("$.codigoGrupo").value("GT-01"))
                .andExpect(jsonPath("$.alertas", hasSize(0)));
    }
}