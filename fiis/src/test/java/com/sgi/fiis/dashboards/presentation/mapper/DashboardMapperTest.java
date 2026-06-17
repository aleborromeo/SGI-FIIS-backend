package com.sgi.fiis.dashboards.presentation.mapper;

import com.sgi.fiis.dashboards.application.dto.*;
import com.sgi.fiis.dashboards.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DashboardMapper Unit Tests")
class DashboardMapperTest {

    private DashboardMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DashboardMapper();
    }

    @Test
    @DisplayName("Debe mapear DashboardAdmin a DashboardAdminResponse")
    void toAdminResponse_debeMapearCamposYAlertas() {
        AlertaItem alerta = AlertaItem.builder()
                .tipo("REVISION")
                .titulo("Trámite pendiente")
                .descripcion("Hay trámites pendientes")
                .build();

        DashboardAdmin modelo = DashboardAdmin.builder()
                .totalUsuarios(10)
                .totalUsuariosActivos(8)
                .totalGrupos(5)
                .totalGruposActivos(4)
                .totalProyectos(20)
                .proyectosActivos(12)
                .tramitesPendientes(3)
                .resolucionesEmitidas(7)
                .tramitesEnRevision(2)
                .tramitesAprobados(9)
                .tramitesRechazados(1)
                .alertas(List.of(alerta))
                .build();

        DashboardAdminResponse response = mapper.toAdminResponse(modelo);

        assertEquals(10, response.getTotalUsuarios());
        assertEquals(8, response.getTotalUsuariosActivos());
        assertEquals(5, response.getTotalGrupos());
        assertEquals(4, response.getTotalGruposActivos());
        assertEquals(20, response.getTotalProyectos());
        assertEquals(12, response.getProyectosActivos());
        assertEquals(3, response.getTramitesPendientes());
        assertEquals(7, response.getResolucionesEmitidas());
        assertEquals(2, response.getTramitesEnRevision());
        assertEquals(9, response.getTramitesAprobados());
        assertEquals(1, response.getTramitesRechazados());

        assertEquals(1, response.getAlertas().size());
        assertEquals("REVISION", response.getAlertas().get(0).getTipo());
        assertEquals("Trámite pendiente", response.getAlertas().get(0).getTitulo());
        assertEquals("Hay trámites pendientes", response.getAlertas().get(0).getDescripcion());

        DashboardAdmin sinAlertas = DashboardAdmin.builder().alertas(null).build();
        assertTrue(mapper.toAdminResponse(sinAlertas).getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe mapear DashboardDirector a DashboardDirectorResponse")
    void toDirectorResponse_debeMapearCamposYAlertas() {
        AlertaItem alerta = AlertaItem.builder()
                .tipo("INFO")
                .titulo("Convocatoria activa")
                .descripcion("Hay convocatoria")
                .build();

        DashboardDirector modelo = DashboardDirector.builder()
                .totalProyectos(10)
                .proyectosActivos(6)
                .proyectosPostulados(3)
                .proyectosObservados(1)
                .tramitesPendientesRevision(4)
                .informesPorVencer(2)
                .resolucionesEmitidas(9)
                .convocatoriasAbiertas(1)
                .tramitesEnCoordinador(2)
                .tramitesEnDirector(3)
                .tramitesEnDecano(4)
                .tramitesFinalizados(5)
                .alertas(List.of(alerta))
                .build();

        DashboardDirectorResponse response = mapper.toDirectorResponse(modelo);

        assertEquals(10, response.getTotalProyectos());
        assertEquals(6, response.getProyectosActivos());
        assertEquals(3, response.getProyectosPostulados());
        assertEquals(1, response.getProyectosObservados());
        assertEquals(4, response.getTramitesPendientesRevision());
        assertEquals(2, response.getInformesPorVencer());
        assertEquals(9, response.getResolucionesEmitidas());
        assertEquals(1, response.getConvocatoriasAbiertas());
        assertEquals(2, response.getTramitesEnCoordinador());
        assertEquals(3, response.getTramitesEnDirector());
        assertEquals(4, response.getTramitesEnDecano());
        assertEquals(5, response.getTramitesFinalizados());

        assertEquals(1, response.getAlertas().size());
        assertEquals("INFO", response.getAlertas().get(0).getTipo());

        DashboardDirector sinAlertas = DashboardDirector.builder().alertas(null).build();
        assertTrue(mapper.toDirectorResponse(sinAlertas).getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe mapear DashboardCoordinador a DashboardCoordinadorResponse")
    void toCoordinadorResponse_debeMapearCamposYAlertas() {
        AlertaItem alerta = AlertaItem.builder()
                .tipo("ALERTA")
                .titulo("Trámite observado")
                .descripcion("Hay observaciones")
                .build();

        DashboardCoordinador modelo = DashboardCoordinador.builder()
                .idGrupo(1)
                .nombreGrupo("Grupo FIIS")
                .codigoGrupo("GI-FIIS")
                .totalMiembros(10)
                .miembrosActivos(8)
                .totalProyectosGrupo(4)
                .proyectosActivosGrupo(3)
                .tramitesPendientesGrupo(2)
                .informesAvanceGrupo(5)
                .planesTesisGrupo(6)
                .tramitesPostulados(1)
                .tramitesEnRevision(2)
                .tramitesAprobados(3)
                .tramitesObservados(4)
                .alertas(List.of(alerta))
                .build();

        DashboardCoordinadorResponse response = mapper.toCoordinadorResponse(modelo);

        assertEquals(1, response.getIdGrupo());
        assertEquals("Grupo FIIS", response.getNombreGrupo());
        assertEquals("GI-FIIS", response.getCodigoGrupo());
        assertEquals(10, response.getTotalMiembros());
        assertEquals(8, response.getMiembrosActivos());
        assertEquals(4, response.getTotalProyectosGrupo());
        assertEquals(3, response.getProyectosActivosGrupo());
        assertEquals(2, response.getTramitesPendientesGrupo());
        assertEquals(5, response.getInformesAvanceGrupo());
        assertEquals(6, response.getPlanesTesisGrupo());
        assertEquals(1, response.getTramitesPostulados());
        assertEquals(2, response.getTramitesEnRevision());
        assertEquals(3, response.getTramitesAprobados());
        assertEquals(4, response.getTramitesObservados());

        assertEquals(1, response.getAlertas().size());
        assertEquals("ALERTA", response.getAlertas().get(0).getTipo());

        DashboardCoordinador sinAlertas = DashboardCoordinador.builder().alertas(null).build();
        assertTrue(mapper.toCoordinadorResponse(sinAlertas).getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe mapear DashboardDocente a DashboardDocenteResponse")
    void toDocenteResponse_debeMapearCamposYAlertas() {
        AlertaItem alerta = AlertaItem.builder()
                .tipo("REVISION")
                .titulo("Informe pendiente")
                .descripcion("Tiene informes")
                .build();

        DashboardDocente modelo = DashboardDocente.builder()
                .proyectosComoResponsable(2)
                .proyectosComoIntegrante(3)
                .tramitesPendientes(4)
                .informesAvancePendientes(5)
                .documentosCargados(6)
                .resolucionesRecibidas(7)
                .proyectosPostulados(1)
                .proyectosAprobados(2)
                .proyectosEnEjecucion(3)
                .proyectosFinalizados(4)
                .alertas(List.of(alerta))
                .build();

        DashboardDocenteResponse response = mapper.toDocenteResponse(modelo);

        assertEquals(2, response.getProyectosComoResponsable());
        assertEquals(3, response.getProyectosComoIntegrante());
        assertEquals(4, response.getTramitesPendientes());
        assertEquals(5, response.getInformesAvancePendientes());
        assertEquals(6, response.getDocumentosCargados());
        assertEquals(7, response.getResolucionesRecibidas());
        assertEquals(1, response.getProyectosPostulados());
        assertEquals(2, response.getProyectosAprobados());
        assertEquals(3, response.getProyectosEnEjecucion());
        assertEquals(4, response.getProyectosFinalizados());

        assertEquals(1, response.getAlertas().size());
        assertEquals("REVISION", response.getAlertas().get(0).getTipo());

        DashboardDocente sinAlertas = DashboardDocente.builder().alertas(null).build();
        assertTrue(mapper.toDocenteResponse(sinAlertas).getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe mapear DashboardEvaluador a DashboardEvaluadorResponse")
    void toEvaluadorResponse_debeMapearCamposYAlertas() {
        AlertaItem alerta = AlertaItem.builder()
                .tipo("REVISION")
                .titulo("Evaluaciones pendientes")
                .descripcion("Tiene evaluaciones")
                .build();

        DashboardEvaluador modelo = DashboardEvaluador.builder()
                .evaluacionesAsignadas(10)
                .evaluacionesPendientes(3)
                .evaluacionesCompletadas(7)
                .proyectosAsignados(4)
                .planesTesisAsignados(5)
                .evaluacionesAprobadas(2)
                .evaluacionesRechazadas(1)
                .evaluacionesConObservaciones(6)
                .alertas(List.of(alerta))
                .build();

        DashboardEvaluadorResponse response = mapper.toEvaluadorResponse(modelo);

        assertEquals(10, response.getEvaluacionesAsignadas());
        assertEquals(3, response.getEvaluacionesPendientes());
        assertEquals(7, response.getEvaluacionesCompletadas());
        assertEquals(4, response.getProyectosAsignados());
        assertEquals(5, response.getPlanesTesisAsignados());
        assertEquals(2, response.getEvaluacionesAprobadas());
        assertEquals(1, response.getEvaluacionesRechazadas());
        assertEquals(6, response.getEvaluacionesConObservaciones());

        assertEquals(1, response.getAlertas().size());
        assertEquals("REVISION", response.getAlertas().get(0).getTipo());

        DashboardEvaluador sinAlertas = DashboardEvaluador.builder().alertas(null).build();
        assertTrue(mapper.toEvaluadorResponse(sinAlertas).getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe mapear DashboardDecano a DashboardDecanoResponse")
    void toDecanoResponse_debeMapearCamposYAlertas() {
        AlertaItem alerta = AlertaItem.builder()
                .tipo("INFO")
                .titulo("Convocatoria activa")
                .descripcion("Hay convocatoria")
                .build();

        DashboardDecano modelo = DashboardDecano.builder()
                .totalProyectosFacultad(20)
                .proyectosActivos(12)
                .tramitesPendientesFirma(4)
                .resolucionesEmitidas(9)
                .convocatoriasActivas(2)
                .totalGruposActivos(6)
                .tramitesEnEspera(3)
                .tramitesAprobadosMes(5)
                .tramitesRechazadosMes(1)
                .alertas(List.of(alerta))
                .build();

        DashboardDecanoResponse response = mapper.toDecanoResponse(modelo);

        assertEquals(20, response.getTotalProyectosFacultad());
        assertEquals(12, response.getProyectosActivos());
        assertEquals(4, response.getTramitesPendientesFirma());
        assertEquals(9, response.getResolucionesEmitidas());
        assertEquals(2, response.getConvocatoriasActivas());
        assertEquals(6, response.getTotalGruposActivos());
        assertEquals(3, response.getTramitesEnEspera());
        assertEquals(5, response.getTramitesAprobadosMes());
        assertEquals(1, response.getTramitesRechazadosMes());

        assertEquals(1, response.getAlertas().size());
        assertEquals("INFO", response.getAlertas().get(0).getTipo());

        DashboardDecano sinAlertas = DashboardDecano.builder().alertas(null).build();
        assertTrue(mapper.toDecanoResponse(sinAlertas).getAlertas().isEmpty());
    }

    @Test
    @DisplayName("Debe mapear DashboardEstudiante a DashboardEstudianteResponse")
    void toEstudianteResponse_debeMapearCamposYAlertas() {
        AlertaItem alerta = AlertaItem.builder()
                .tipo("ALERTA")
                .titulo("Plan observado")
                .descripcion("Debe subsanar")
                .build();

        DashboardEstudiante modelo = DashboardEstudiante.builder()
                .planesTesisPresentados(1)
                .estadoPlanActual("OBSERVADO")
                .tramitesPendientes(2)
                .documentosCargados(3)
                .convocatoriasAbiertas(4)
                .nombreGrupo("Grupo Tesis")
                .codigoGrupo("GT-01")
                .alertas(List.of(alerta))
                .build();

        DashboardEstudianteResponse response = mapper.toEstudianteResponse(modelo);

        assertEquals(1, response.getPlanesTesisPresentados());
        assertEquals("OBSERVADO", response.getEstadoPlanActual());
        assertEquals(2, response.getTramitesPendientes());
        assertEquals(3, response.getDocumentosCargados());
        assertEquals(4, response.getConvocatoriasAbiertas());
        assertEquals("Grupo Tesis", response.getNombreGrupo());
        assertEquals("GT-01", response.getCodigoGrupo());

        assertEquals(1, response.getAlertas().size());
        assertEquals("ALERTA", response.getAlertas().get(0).getTipo());

        DashboardEstudiante sinAlertas = DashboardEstudiante.builder().alertas(null).build();
        assertTrue(mapper.toEstudianteResponse(sinAlertas).getAlertas().isEmpty());
    }
}