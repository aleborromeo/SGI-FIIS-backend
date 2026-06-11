package com.sgi.fiis.dashboards.presentation.mapper;

import com.sgi.fiis.dashboards.application.dto.*;
import com.sgi.fiis.dashboards.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DashboardMapper {

    // =========================================================
    // ADMIN
    // =========================================================

    public DashboardAdminResponse toAdminResponse(DashboardAdmin model) {

        if (model == null) {
            return null;
        }

        return DashboardAdminResponse.builder()
                .totalUsuarios(model.getTotalUsuarios())
                .totalUsuariosActivos(model.getTotalUsuariosActivos())
                .totalGrupos(model.getTotalGrupos())
                .totalGruposActivos(model.getTotalGruposActivos())
                .totalProyectos(model.getTotalProyectos())
                .proyectosActivos(model.getProyectosActivos())
                .tramitesPendientes(model.getTramitesPendientes())
                .resolucionesEmitidas(model.getResolucionesEmitidas())
                .tramitesEnRevision(model.getTramitesEnRevision())
                .tramitesAprobados(model.getTramitesAprobados())
                .tramitesRechazados(model.getTramitesRechazados())
                .alertas(model.getAlertas() == null
                        ? Collections.emptyList()
                        : model.getAlertas().stream()
                        .map(a -> DashboardAdminResponse.AlertaItemResponse.builder()
                                .tipo(a.getTipo())
                                .titulo(a.getTitulo())
                                .descripcion(a.getDescripcion())
                                .build())
                        .toList())
                .build();
    }

    // =========================================================
    // DIRECTOR
    // =========================================================

    public DashboardDirectorResponse toDirectorResponse(DashboardDirector model) {

        if (model == null) {
            return null;
        }

        return DashboardDirectorResponse.builder()
                .totalProyectos(model.getTotalProyectos())
                .proyectosActivos(model.getProyectosActivos())
                .proyectosPostulados(model.getProyectosPostulados())
                .proyectosObservados(model.getProyectosObservados())
                .tramitesPendientesRevision(model.getTramitesPendientesRevision())
                .informesPorVencer(model.getInformesPorVencer())
                .resolucionesEmitidas(model.getResolucionesEmitidas())
                .convocatoriasAbiertas(model.getConvocatoriasAbiertas())
                .tramitesEnCoordinador(model.getTramitesEnCoordinador())
                .tramitesEnDirector(model.getTramitesEnDirector())
                .tramitesEnDecano(model.getTramitesEnDecano())
                .tramitesFinalizados(model.getTramitesFinalizados())
                .alertas(model.getAlertas() == null
                        ? Collections.emptyList()
                        : model.getAlertas().stream()
                        .map(a -> DashboardDirectorResponse.AlertaItemResponse.builder()
                                .tipo(a.getTipo())
                                .titulo(a.getTitulo())
                                .descripcion(a.getDescripcion())
                                .build())
                        .toList())
                .build();
    }

    // =========================================================
    // COORDINADOR
    // =========================================================

    public DashboardCoordinadorResponse toCoordinadorResponse(DashboardCoordinador model) {

        if (model == null) {
            return null;
        }

        return DashboardCoordinadorResponse.builder()
                .idGrupo(model.getIdGrupo())
                .nombreGrupo(model.getNombreGrupo())
                .codigoGrupo(model.getCodigoGrupo())
                .totalMiembros(model.getTotalMiembros())
                .miembrosActivos(model.getMiembrosActivos())
                .totalProyectosGrupo(model.getTotalProyectosGrupo())
                .proyectosActivosGrupo(model.getProyectosActivosGrupo())
                .tramitesPendientesGrupo(model.getTramitesPendientesGrupo())
                .informesAvanceGrupo(model.getInformesAvanceGrupo())
                .planesTesisGrupo(model.getPlanesTesisGrupo())
                .tramitesPostulados(model.getTramitesPostulados())
                .tramitesEnRevision(model.getTramitesEnRevision())
                .tramitesAprobados(model.getTramitesAprobados())
                .tramitesObservados(model.getTramitesObservados())
                .alertas(model.getAlertas() == null
                        ? Collections.emptyList()
                        : model.getAlertas().stream()
                        .map(a -> DashboardCoordinadorResponse.AlertaItemResponse.builder()
                                .tipo(a.getTipo())
                                .titulo(a.getTitulo())
                                .descripcion(a.getDescripcion())
                                .build())
                        .toList())
                .build();
    }

    // =========================================================
    // DOCENTE
    // =========================================================

    public DashboardDocenteResponse toDocenteResponse(DashboardDocente model) {

        if (model == null) {
            return null;
        }

        return DashboardDocenteResponse.builder()
                .proyectosComoResponsable(model.getProyectosComoResponsable())
                .proyectosComoIntegrante(model.getProyectosComoIntegrante())
                .tramitesPendientes(model.getTramitesPendientes())
                .informesAvancePendientes(model.getInformesAvancePendientes())
                .documentosCargados(model.getDocumentosCargados())
                .resolucionesRecibidas(model.getResolucionesRecibidas())
                .proyectosPostulados(model.getProyectosPostulados())
                .proyectosAprobados(model.getProyectosAprobados())
                .proyectosEnEjecucion(model.getProyectosEnEjecucion())
                .proyectosFinalizados(model.getProyectosFinalizados())
                .alertas(model.getAlertas() == null
                        ? Collections.emptyList()
                        : model.getAlertas().stream()
                        .map(a -> DashboardDocenteResponse.AlertaItemResponse.builder()
                                .tipo(a.getTipo())
                                .titulo(a.getTitulo())
                                .descripcion(a.getDescripcion())
                                .build())
                        .toList())
                .build();
    }

    // =========================================================
    // EVALUADOR
    // =========================================================

    public DashboardEvaluadorResponse toEvaluadorResponse(DashboardEvaluador model) {

        if (model == null) {
            return null;
        }

        return DashboardEvaluadorResponse.builder()
                .evaluacionesAsignadas(model.getEvaluacionesAsignadas())
                .evaluacionesPendientes(model.getEvaluacionesPendientes())
                .evaluacionesCompletadas(model.getEvaluacionesCompletadas())
                .proyectosAsignados(model.getProyectosAsignados())
                .planesTesisAsignados(model.getPlanesTesisAsignados())
                .evaluacionesAprobadas(model.getEvaluacionesAprobadas())
                .evaluacionesRechazadas(model.getEvaluacionesRechazadas())
                .evaluacionesConObservaciones(model.getEvaluacionesConObservaciones())
                .alertas(model.getAlertas() == null
                        ? Collections.emptyList()
                        : model.getAlertas().stream()
                        .map(a -> DashboardEvaluadorResponse.AlertaItemResponse.builder()
                                .tipo(a.getTipo())
                                .titulo(a.getTitulo())
                                .descripcion(a.getDescripcion())
                                .build())
                        .toList())
                .build();
    }

    // =========================================================
    // DECANO
    // =========================================================

    public DashboardDecanoResponse toDecanoResponse(DashboardDecano model) {

        if (model == null) {
            return null;
        }

        return DashboardDecanoResponse.builder()
                .totalProyectosFacultad(model.getTotalProyectosFacultad())
                .proyectosActivos(model.getProyectosActivos())
                .tramitesPendientesFirma(model.getTramitesPendientesFirma())
                .resolucionesEmitidas(model.getResolucionesEmitidas())
                .convocatoriasActivas(model.getConvocatoriasActivas())
                .totalGruposActivos(model.getTotalGruposActivos())
                .tramitesEnEspera(model.getTramitesEnEspera())
                .tramitesAprobadosMes(model.getTramitesAprobadosMes())
                .tramitesRechazadosMes(model.getTramitesRechazadosMes())
                .alertas(model.getAlertas() == null
                        ? Collections.emptyList()
                        : model.getAlertas().stream()
                        .map(a -> DashboardDecanoResponse.AlertaItemResponse.builder()
                                .tipo(a.getTipo())
                                .titulo(a.getTitulo())
                                .descripcion(a.getDescripcion())
                                .build())
                        .toList())
                .build();
    }

    // =========================================================
    // ESTUDIANTE
    // =========================================================

    public DashboardEstudianteResponse toEstudianteResponse(DashboardEstudiante model) {

        if (model == null) {
            return null;
        }

        return DashboardEstudianteResponse.builder()
                .planesTesisPresentados(model.getPlanesTesisPresentados())
                .estadoPlanActual(model.getEstadoPlanActualDescripcion())
                .tramitesPendientes(model.getTramitesPendientes())
                .documentosCargados(model.getDocumentosCargados())
                .convocatoriasAbiertas(model.getConvocatoriasAbiertas())
                .nombreGrupo(model.getNombreGrupo())
                .codigoGrupo(model.getCodigoGrupo())
                .alertas(model.getAlertas() == null
                        ? Collections.emptyList()
                        : model.getAlertas().stream()
                        .map(a -> DashboardEstudianteResponse.AlertaItemResponse.builder()
                                .tipo(a.getTipo())
                                .titulo(a.getTitulo())
                                .descripcion(a.getDescripcion())
                                .build())
                        .toList())
                .build();
    }
}