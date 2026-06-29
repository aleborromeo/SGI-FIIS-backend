package com.sgi.fiis.dashboards.presentation.controller;

import com.sgi.fiis.dashboards.application.dto.*;
import com.sgi.fiis.dashboards.application.usecase.*;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetAdminDashboardUseCase adminDashboardUseCase;
    private final GetDirectorDashboardUseCase directorDashboardUseCase;
    private final GetCoordinatorDashboardUseCase coordinatorDashboardUseCase;
    private final GetTeacherDashboardUseCase teacherDashboardUseCase;
    private final GetEvaluatorDashboardUseCase evaluatorDashboardUseCase;
    private final GetDeanDashboardUseCase deanDashboardUseCase;
    private final GetStudentDashboardUseCase studentDashboardUseCase;

    /**
     * Unified Dashboard Endpoint (RF-88 a RF-94 / RNF-08)
     * Automatically detects role and user ID from token to serve appropriate dashboard.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getMyDashboard(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = userDetails.getId().intValue();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        switch (role) {
            case "ADMIN":
                return ResponseEntity.ok(adminDashboardUseCase.execute(userId));
            case "DIRECTOR_INVESTIGACION":
                return ResponseEntity.ok(directorDashboardUseCase.execute(userId));
            case "COORDINADOR_GRUPO":
                return ResponseEntity.ok(coordinatorDashboardUseCase.execute(userId));
            case "DOCENTE_INVESTIGADOR":
                return ResponseEntity.ok(teacherDashboardUseCase.execute(userId));
            case "EVALUADOR":
                return ResponseEntity.ok(evaluatorDashboardUseCase.execute(userId));
            case "DECANO":
                return ResponseEntity.ok(deanDashboardUseCase.execute(userId));
            case "ESTUDIANTE":
                return ResponseEntity.ok(studentDashboardUseCase.execute(userId));
            default:
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Rol de usuario no soportado para panel de control.");
        }
    }

    /**
     * RF-88: Dashboard for the System Administrator.
     * Global view: users, groups, projects, procedures and resolutions.
     */
    @GetMapping("/admin/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN')")
    public ResponseEntity<DashboardAdminResponse> getAdminDashboard(
            @PathVariable Integer userId,
            Authentication authentication) {
        validateAccess(userId, authentication, "ADMIN");
        return ResponseEntity.ok(adminDashboardUseCase.execute(userId));
    }

    /**
     * RF-89: Institutional dashboard for the Research Director.
     * View of projects, procedures by stage, reports and calls for applications.
     */
    @GetMapping("/director/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION')")
    public ResponseEntity<DashboardDirectorResponse> getDirectorDashboard(
            @PathVariable Integer userId,
            Authentication authentication) {
        validateAccess(userId, authentication, "DIRECTOR_INVESTIGACION");
        return ResponseEntity.ok(directorDashboardUseCase.execute(userId));
    }

    /**
     * RF-90, RF-91: Dashboard for the Group Coordinator.
     * Shows only data for their own group: members, projects, procedures and reports.
     */
    @GetMapping("/coordinator/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR_GRUPO')")
    public ResponseEntity<DashboardCoordinatorResponse> getCoordinatorDashboard(
            @PathVariable Integer userId,
            Authentication authentication) {
        validateAccess(userId, authentication, "COORDINADOR_GRUPO");
        return ResponseEntity.ok(coordinatorDashboardUseCase.execute(userId));
    }

    /**
     * RF-92: Dashboard for the Research Teacher.
     * Own projects, documents, procedures and progress reports.
     */
    @GetMapping("/teacher/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE_INVESTIGADOR')")
    public ResponseEntity<DashboardTeacherResponse> getTeacherDashboard(
            @PathVariable Integer userId,
            Authentication authentication) {
        validateAccess(userId, authentication, "DOCENTE_INVESTIGADOR");
        return ResponseEntity.ok(teacherDashboardUseCase.execute(userId));
    }

    /**
     * RF-93: Dashboard for the Evaluator.
     * Assigned projects and thesis plans for evaluation.
     */
    @GetMapping("/evaluator/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')")
    public ResponseEntity<DashboardEvaluatorResponse> getEvaluatorDashboard(
            @PathVariable Integer userId,
            Authentication authentication) {
        validateAccess(userId, authentication, "EVALUADOR");
        return ResponseEntity.ok(evaluatorDashboardUseCase.execute(userId));
    }

    /**
     * Dashboard for the Dean.
     * Faculty view: procedures pending signature, resolutions and calls for applications.
     */
    @GetMapping("/dean/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DECANO')")
    public ResponseEntity<DashboardDeanResponse> getDeanDashboard(
            @PathVariable Integer userId,
            Authentication authentication) {
        validateAccess(userId, authentication, "DECANO");
        return ResponseEntity.ok(deanDashboardUseCase.execute(userId));
    }

    /**
     * Dashboard for the Student / Thesis candidate.
     * Status of their thesis plan, group, procedures and active calls.
     */
    @GetMapping("/student/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<DashboardStudentResponse> getStudentDashboard(
            @PathVariable Integer userId,
            Authentication authentication) {
        validateAccess(userId, authentication, "ESTUDIANTE");
        return ResponseEntity.ok(studentDashboardUseCase.execute(userId));
    }

    // --- Helper de Validación de Acceso y Propiedad de Recurso ---
    private void validateAccess(Integer targetUserId, Authentication authentication, String requiredRole) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new org.springframework.security.access.AccessDeniedException("Acceso denegado: Usuario no autenticado.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            boolean hasRequiredRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_" + requiredRole));
            if (!hasRequiredRole) {
                throw new org.springframework.security.access.AccessDeniedException("Acceso denegado: No posee el rol requerido para este panel.");
            }

            if (!userDetails.getId().equals(targetUserId.longValue())) {
                throw new org.springframework.security.access.AccessDeniedException("Acceso denegado: No puede consultar datos de otro usuario.");
            }
        }
    }
}