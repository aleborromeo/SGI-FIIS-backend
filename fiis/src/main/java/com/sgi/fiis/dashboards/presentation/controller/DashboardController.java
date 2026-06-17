package com.sgi.fiis.dashboards.presentation.controller;

import com.sgi.fiis.dashboards.application.dto.*;
import com.sgi.fiis.dashboards.application.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ObtenerDashboardAdminUseCase dashboardAdminUseCase;
    private final ObtenerDashboardDirectorUseCase dashboardDirectorUseCase;
    private final ObtenerDashboardCoordinadorUseCase dashboardCoordinadorUseCase;
    private final ObtenerDashboardDocenteUseCase dashboardDocenteUseCase;
    private final ObtenerDashboardEvaluadorUseCase dashboardEvaluadorUseCase;
    private final ObtenerDashboardDecanoUseCase dashboardDecanoUseCase;
    private final ObtenerDashboardEstudianteUseCase dashboardEstudianteUseCase;

    /**
     * RF-88: Dashboard para el Administrador del Sistema.
     * Visión global: usuarios, grupos, proyectos, trámites y resoluciones.
     */
    @GetMapping("/admin/{idUsuario}")
    public ResponseEntity<DashboardAdminResponse> getDashboardAdmin(
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(dashboardAdminUseCase.ejecutar(idUsuario));
    }

    /**
     * RF-89: Dashboard institucional para el Director de Investigación.
     * Visión de proyectos, trámites por etapa, informes y convocatorias.
     */
    @GetMapping("/director/{idUsuario}")
    public ResponseEntity<DashboardDirectorResponse> getDashboardDirector(
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(dashboardDirectorUseCase.ejecutar(idUsuario));
    }

    /**
     * RF-90, RF-91: Dashboard para el Coordinador de Grupo.
     * Solo muestra datos de su propio grupo: miembros, proyectos, trámites e informes.
     */
    @GetMapping("/coordinador/{idUsuario}")
    public ResponseEntity<DashboardCoordinadorResponse> getDashboardCoordinador(
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(dashboardCoordinadorUseCase.ejecutar(idUsuario));
    }

    /**
     * RF-92: Dashboard para el Docente Investigador.
     * Proyectos propios, documentos, trámites e informes de avance.
     */
    @GetMapping("/docente/{idUsuario}")
    public ResponseEntity<DashboardDocenteResponse> getDashboardDocente(
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(dashboardDocenteUseCase.ejecutar(idUsuario));
    }

    /**
     * RF-93: Dashboard para el Evaluador.
     * Proyectos y planes de tesis asignados para evaluación.
     */
    @GetMapping("/evaluador/{idUsuario}")
    public ResponseEntity<DashboardEvaluadorResponse> getDashboardEvaluador(
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(dashboardEvaluadorUseCase.ejecutar(idUsuario));
    }

    /**
     * Dashboard para el Decano.
     * Visión facultad: trámites pendientes de firma, resoluciones y convocatorias.
     */
    @GetMapping("/decano/{idUsuario}")
    public ResponseEntity<DashboardDecanoResponse> getDashboardDecano(
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(dashboardDecanoUseCase.ejecutar(idUsuario));
    }

    /**
     * Dashboard para el Estudiante / Tesista.
     * Estado de su plan de tesis, grupo, trámites y convocatorias activas.
     */
    @GetMapping("/estudiante/{idUsuario}")
    public ResponseEntity<DashboardEstudianteResponse> getDashboardEstudiante(
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(dashboardEstudianteUseCase.ejecutar(idUsuario));
    }
}