package com.sgi.fiis.dashboards.infrastructure.persistence;

import com.sgi.fiis.dashboards.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardRepositoryImpl Unit Tests")
class DashboardRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private DashboardRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new DashboardRepositoryImpl(jdbcTemplate);
    }

    private void mockAllCounts(int value) {
        lenient()
                .when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(value);
    }

    @Test
    @DisplayName("Debe obtener dashboard admin con métricas y alertas")
    void obtenerDashboardAdmin_debeRetornarMetricasYAlertas() {
        mockAllCounts(1);

        DashboardAdmin result = repository.obtenerDashboardAdmin(1);

        assertNotNull(result);
        assertEquals(1, result.getTotalUsuarios());
        assertEquals(1, result.getTotalUsuariosActivos());
        assertEquals(1, result.getTotalGrupos());
        assertEquals(1, result.getTotalGruposActivos());
        assertEquals(1, result.getTotalProyectos());
        assertEquals(1, result.getProyectosActivos());
        assertEquals(1, result.getTramitesPendientes());
        assertEquals(1, result.getResolucionesEmitidas());
        assertEquals(1, result.getTramitesEnRevision());
        assertEquals(1, result.getTramitesAprobados());
        assertEquals(1, result.getTramitesRechazados());
        assertNotNull(result.getAlertas());
        assertFalse(result.getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener dashboard director con métricas y alertas")
    void obtenerDashboardDirector_debeRetornarMetricasYAlertas() {
        mockAllCounts(1);

        DashboardDirector result = repository.obtenerDashboardDirector(1);

        assertNotNull(result);
        assertEquals(1, result.getTotalProyectos());
        assertEquals(1, result.getProyectosActivos());
        assertEquals(1, result.getProyectosPostulados());
        assertEquals(1, result.getProyectosObservados());
        assertEquals(1, result.getTramitesPendientesRevision());
        assertEquals(1, result.getInformesPorVencer());
        assertEquals(1, result.getResolucionesEmitidas());
        assertEquals(1, result.getConvocatoriasAbiertas());
        assertEquals(1, result.getTramitesEnCoordinador());
        assertEquals(1, result.getTramitesEnDirector());
        assertEquals(1, result.getTramitesEnDecano());
        assertEquals(1, result.getTramitesFinalizados());
        assertNotNull(result.getAlertas());
        assertFalse(result.getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener dashboard coordinador cuando tiene grupo asignado")
    void obtenerDashboardCoordinador_conGrupo_debeRetornarMetricas() {
        mockAllCounts(1);

        Map<String, Object> grupo = Map.of(
                "id_grupo", 10,
                "nombre_grupo", "Grupo de Investigación FIIS",
                "codigo_grupo", "GI-FIIS"
        );

        when(jdbcTemplate.queryForList(anyString(), eq(1)))
                .thenReturn(List.of(grupo));

        DashboardCoordinador result = repository.obtenerDashboardCoordinador(1);

        assertNotNull(result);
        assertEquals(10, result.getIdGrupo());
        assertEquals("Grupo de Investigación FIIS", result.getNombreGrupo());
        assertEquals("GI-FIIS", result.getCodigoGrupo());
        assertEquals(1, result.getTotalMiembros());
        assertEquals(1, result.getMiembrosActivos());
        assertEquals(1, result.getTotalProyectosGrupo());
        assertEquals(1, result.getProyectosActivosGrupo());
        assertEquals(1, result.getTramitesPendientesGrupo());
        assertEquals(1, result.getInformesAvanceGrupo());
        assertEquals(1, result.getPlanesTesisGrupo());
        assertEquals(1, result.getTramitesPostulados());
        assertEquals(1, result.getTramitesEnRevision());
        assertEquals(1, result.getTramitesAprobados());
        assertEquals(1, result.getTramitesObservados());
        assertNotNull(result.getAlertas());
        assertFalse(result.getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe retornar alerta cuando coordinador no tiene grupo asignado")
    void obtenerDashboardCoordinador_sinGrupo_debeRetornarAlerta() {
        mockAllCounts(1);

        when(jdbcTemplate.queryForList(anyString(), eq(1)))
                .thenReturn(Collections.emptyList());

        DashboardCoordinador result = repository.obtenerDashboardCoordinador(1);

        assertNotNull(result);
        assertEquals("Sin grupo asignado", result.getNombreGrupo());
        assertEquals("", result.getCodigoGrupo());
        assertNotNull(result.getAlertas());
        assertEquals(1, result.getAlertas().size());
        assertEquals("Sin grupo asignado", result.getAlertas().get(0).getTitulo());
    }

    @Test
    @DisplayName("Debe obtener dashboard docente con métricas y alertas")
    void obtenerDashboardDocente_debeRetornarMetricasYAlertas() {
        mockAllCounts(1);

        DashboardDocente result = repository.obtenerDashboardDocente(1);

        assertNotNull(result);
        assertEquals(1, result.getProyectosComoResponsable());
        assertEquals(1, result.getProyectosComoIntegrante());
        assertEquals(1, result.getTramitesPendientes());
        assertEquals(1, result.getInformesAvancePendientes());
        assertEquals(1, result.getDocumentosCargados());
        assertEquals(1, result.getResolucionesRecibidas());
        assertEquals(1, result.getProyectosPostulados());
        assertEquals(1, result.getProyectosAprobados());
        assertEquals(1, result.getProyectosEnEjecucion());
        assertEquals(1, result.getProyectosFinalizados());
        assertNotNull(result.getAlertas());
        assertFalse(result.getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener dashboard evaluador con métricas y alertas")
    void obtenerDashboardEvaluador_debeRetornarMetricasYAlertas() {
        mockAllCounts(1);

        DashboardEvaluador result = repository.obtenerDashboardEvaluador(1);

        assertNotNull(result);
        assertEquals(1, result.getEvaluacionesAsignadas());
        assertEquals(1, result.getEvaluacionesPendientes());
        assertEquals(1, result.getEvaluacionesCompletadas());
        assertEquals(1, result.getProyectosAsignados());
        assertEquals(1, result.getPlanesTesisAsignados());
        assertEquals(1, result.getEvaluacionesAprobadas());
        assertEquals(1, result.getEvaluacionesRechazadas());
        assertEquals(1, result.getEvaluacionesConObservaciones());
        assertNotNull(result.getAlertas());
        assertFalse(result.getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener dashboard decano con métricas y alertas")
    void obtenerDashboardDecano_debeRetornarMetricasYAlertas() {
        mockAllCounts(1);

        DashboardDecano result = repository.obtenerDashboardDecano(1);

        assertNotNull(result);
        assertEquals(1, result.getTotalProyectosFacultad());
        assertEquals(1, result.getProyectosActivos());
        assertEquals(1, result.getTramitesPendientesFirma());
        assertEquals(1, result.getResolucionesEmitidas());
        assertEquals(1, result.getConvocatoriasActivas());
        assertEquals(1, result.getTotalGruposActivos());
        assertEquals(1, result.getTramitesEnEspera());
        assertEquals(1, result.getTramitesAprobadosMes());
        assertEquals(1, result.getTramitesRechazadosMes());
        assertNotNull(result.getAlertas());
        assertFalse(result.getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener dashboard estudiante con plan observado y grupo asignado")
    void obtenerDashboardEstudiante_conPlanYGrupo_debeRetornarMetricasYAlertas() {
        mockAllCounts(1);

        when(jdbcTemplate.queryForList(anyString(), eq(1)))
                .thenAnswer(invocation -> {
                    String sql = invocation.getArgument(0, String.class);

                    if (sql.contains("estado_plan")) {
                        return List.of(Map.<String, Object>of(
                                "estado_plan", "OBSERVADO"
                        ));
                    }

                    if (sql.contains("membresias_grupo")) {
                        return List.of(Map.<String, Object>of(
                                "nombre_grupo", "Grupo de Tesis",
                                "codigo_grupo", "GT-01"
                        ));
                    }

                    return Collections.emptyList();
                });

        DashboardEstudiante result = repository.obtenerDashboardEstudiante(1);

        assertNotNull(result);
        assertEquals(1, result.getPlanesTesisPresentados());
        assertEquals("OBSERVADO", result.getEstadoPlanActual());
        assertEquals(1, result.getTramitesPendientes());
        assertEquals(1, result.getDocumentosCargados());
        assertEquals(1, result.getConvocatoriasAbiertas());
        assertEquals("Grupo de Tesis", result.getNombreGrupo());
        assertEquals("GT-01", result.getCodigoGrupo());
        assertNotNull(result.getAlertas());
        assertFalse(result.getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener dashboard estudiante sin plan ni grupo")
    void obtenerDashboardEstudiante_sinPlanNiGrupo_debeRetornarValoresPorDefecto() {
        mockAllCounts(0);

        when(jdbcTemplate.queryForList(anyString(), eq(1)))
                .thenReturn(Collections.emptyList());

        DashboardEstudiante result = repository.obtenerDashboardEstudiante(1);

        assertNotNull(result);
        assertEquals(0, result.getPlanesTesisPresentados());
        assertEquals("SIN_PLAN", result.getEstadoPlanActual());
        assertEquals(0, result.getTramitesPendientes());
        assertEquals(0, result.getDocumentosCargados());
        assertEquals(0, result.getConvocatoriasAbiertas());
        assertEquals("Sin grupo", result.getNombreGrupo());
        assertEquals("", result.getCodigoGrupo());
        assertNotNull(result.getAlertas());
        assertTrue(result.getAlertas().isEmpty());
    }
}