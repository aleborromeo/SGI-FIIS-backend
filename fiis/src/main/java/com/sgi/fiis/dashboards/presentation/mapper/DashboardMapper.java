package com.sgi.fiis.dashboards.presentation.mapper;

import com.sgi.fiis.dashboards.application.dto.*;
import com.sgi.fiis.dashboards.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardMapper {

    // =========================================================================
    // ADMIN
    // =========================================================================
    public DashboardAdminResponse toAdminResponse(DashboardAdmin modelo) {
        return DashboardAdminResponse.builder()
                .totalUsuarios(modelo.getTotalUsuarios())
                .totalUsuariosActivos(modelo.getTotalUsuariosActivos())
                .totalGrupos(modelo.getTotalGrupos())
                .totalGruposActivos(modelo.getTotalGruposActivos())
                .totalProyectos(modelo.getTotalProyectos())
                .proyectosActivos(modelo.getProyectosActivos())
                .tramitesPendientes(modelo.getTramitesPendientes())
                .resolucionesEmitidas(modelo.getResolucionesEmitidas())
                .tramitesEnRevision(modelo.getTramitesEnRevision())
                .tramitesAprobados(modelo.getTramitesAprobados())
                .tramitesRechazados(modelo.getTramitesRechazados())
                .alertas(mapAlertas(modelo.getAlertas()))
                .build();
    }

    // =========================================================================
    // DIRECTOR
    // =========================================================================
    public DashboardDirectorResponse toDirectorResponse(DashboardDirector modelo) {
        return DashboardDirectorResponse.builder()
                .totalProyectos(modelo.getTotalProyectos())
                .proyectosActivos(modelo.getProyectosActivos())
                .proyectosPostulados(modelo.getProyectosPostulados())
                .proyectosObservados(modelo.getProyectosObservados())
                .tramitesPendientesRevision(modelo.getTramitesPendientesRevision())
                .informesPorVencer(modelo.getInformesPorVencer())
                .resolucionesEmitidas(modelo.getResolucionesEmitidas())
                .convocatoriasAbiertas(modelo.getConvocatoriasAbiertas())
                .tramitesEnCoordinador(modelo.getTramitesEnCoordinador())
                .tramitesEnDirector(modelo.getTramitesEnDirector())
                .tramitesEnDecano(modelo.getTramitesEnDecano())
                .tramitesFinalizados(modelo.getTramitesFinalizados())
                .alertas(mapAlertas(modelo.getAlertas()))
                .build();
    }

    // =========================================================================
    // COORDINADOR
    // =========================================================================
    public DashboardCoordinadorResponse toCoordinadorResponse(DashboardCoordinador modelo) {
        return DashboardCoordinadorResponse.builder()
                .idGrupo(modelo.getIdGrupo())
                .nombreGrupo(modelo.getNombreGrupo())
                .codigoGrupo(modelo.getCodigoGrupo())
                .totalMiembros(modelo.getTotalMiembros())
                .miembrosActivos(modelo.getMiembrosActivos())
                .totalProyectosGrupo(modelo.getTotalProyectosGrupo())
                .proyectosActivosGrupo(modelo.getProyectosActivosGrupo())
                .tramitesPendientesGrupo(modelo.getTramitesPendientesGrupo())
                .informesAvanceGrupo(modelo.getInformesAvanceGrupo())
                .planesTesisGrupo(modelo.getPlanesTesisGrupo())
                .tramitesPostulados(modelo.getTramitesPostulados())
                .tramitesEnRevision(modelo.getTramitesEnRevision())
                .tramitesAprobados(modelo.getTramitesAprobados())
                .tramitesObservados(modelo.getTramitesObservados())
                .alertas(mapAlertas(modelo.getAlertas()))
                .build();
    }

    // =========================================================================
    // DOCENTE
    // =========================================================================
    public DashboardDocenteResponse toDocenteResponse(DashboardDocente modelo) {
        return DashboardDocenteResponse.builder()
                .proyectosComoResponsable(modelo.getProyectosComoResponsable())
                .proyectosComoIntegrante(modelo.getProyectosComoIntegrante())
                .tramitesPendientes(modelo.getTramitesPendientes())
                .informesAvancePendientes(modelo.getInformesAvancePendientes())
                .documentosCargados(modelo.getDocumentosCargados())
                .resolucionesRecibidas(modelo.getResolucionesRecibidas())
                .proyectosPostulados(modelo.getProyectosPostulados())
                .proyectosAprobados(modelo.getProyectosAprobados())
                .proyectosEnEjecucion(modelo.getProyectosEnEjecucion())
                .proyectosFinalizados(modelo.getProyectosFinalizados())
                .alertas(mapAlertas(modelo.getAlertas()))
                .build();
    }

    // =========================================================================
    // EVALUADOR
    // =========================================================================
    public DashboardEvaluadorResponse toEvaluadorResponse(DashboardEvaluador modelo) {
        return DashboardEvaluadorResponse.builder()
                .evaluacionesAsignadas(modelo.getEvaluacionesAsignadas())
                .evaluacionesPendientes(modelo.getEvaluacionesPendientes())
                .evaluacionesCompletadas(modelo.getEvaluacionesCompletadas())
                .proyectosAsignados(modelo.getProyectosAsignados())
                .planesTesisAsignados(modelo.getPlanesTesisAsignados())
                .evaluacionesAprobadas(modelo.getEvaluacionesAprobadas())
                .evaluacionesRechazadas(modelo.getEvaluacionesRechazadas())
                .evaluacionesConObservaciones(modelo.getEvaluacionesConObservaciones())
                .alertas(mapAlertas(modelo.getAlertas()))
                .build();
    }

    // =========================================================================
    // DECANO
    // =========================================================================
    public DashboardDecanoResponse toDecanoResponse(DashboardDecano modelo) {
        return DashboardDecanoResponse.builder()
                .totalProyectosFacultad(modelo.getTotalProyectosFacultad())
                .proyectosActivos(modelo.getProyectosActivos())
                .tramitesPendientesFirma(modelo.getTramitesPendientesFirma())
                .resolucionesEmitidas(modelo.getResolucionesEmitidas())
                .convocatoriasActivas(modelo.getConvocatoriasActivas())
                .totalGruposActivos(modelo.getTotalGruposActivos())
                .tramitesEnEspera(modelo.getTramitesEnEspera())
                .tramitesAprobadosMes(modelo.getTramitesAprobadosMes())
                .tramitesRechazadosMes(modelo.getTramitesRechazadosMes())
                .alertas(mapAlertas(modelo.getAlertas()))
                .build();
    }

    // =========================================================================
    // ESTUDIANTE
    // =========================================================================
    public DashboardEstudianteResponse toEstudianteResponse(DashboardEstudiante modelo) {
        return DashboardEstudianteResponse.builder()
                .planesTesisPresentados(modelo.getPlanesTesisPresentados())
                .estadoPlanActual(modelo.getEstadoPlanActual())
                .tramitesPendientes(modelo.getTramitesPendientes())
                .documentosCargados(modelo.getDocumentosCargados())
                .convocatoriasAbiertas(modelo.getConvocatoriasAbiertas())
                .nombreGrupo(modelo.getNombreGrupo())
                .codigoGrupo(modelo.getCodigoGrupo())
                .alertas(mapAlertas(modelo.getAlertas()))
                .build();
    }

    // =========================================================================
    // ALERTAS
    // =========================================================================
    private List<AlertaItemResponse> mapAlertas(List<AlertaItem> alertas) {
        if (alertas == null) {
            return List.of();
        }

        return alertas.stream()
                .map(alerta -> AlertaItemResponse.builder()
                        .tipo(alerta.getTipo())
                        .titulo(alerta.getTitulo())
                        .descripcion(alerta.getDescripcion())
                        .build())
                .toList();
    }
}