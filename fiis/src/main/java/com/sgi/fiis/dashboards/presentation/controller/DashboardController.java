package com.sgi.fiis.dashboards.presentation.controller;

import com.sgi.fiis.dashboards.application.dto.*;
import com.sgi.fiis.dashboards.application.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     * RF-88: Dashboard for the System Administrator.
     * Global view: users, groups, projects, procedures and resolutions.
     */
    @GetMapping("/admin/{userId}")
    public ResponseEntity<DashboardAdminResponse> getAdminDashboard(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(adminDashboardUseCase.execute(userId));
    }

    /**
     * RF-89: Institutional dashboard for the Research Director.
     * View of projects, procedures by stage, reports and calls for applications.
     */
    @GetMapping("/director/{userId}")
    public ResponseEntity<DashboardDirectorResponse> getDirectorDashboard(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(directorDashboardUseCase.execute(userId));
    }

    /**
     * RF-90, RF-91: Dashboard for the Group Coordinator.
     * Shows only data for their own group: members, projects, procedures and reports.
     */
    @GetMapping("/coordinator/{userId}")
    public ResponseEntity<DashboardCoordinatorResponse> getCoordinatorDashboard(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(coordinatorDashboardUseCase.execute(userId));
    }

    /**
     * RF-92: Dashboard for the Research Teacher.
     * Own projects, documents, procedures and progress reports.
     */
    @GetMapping("/teacher/{userId}")
    public ResponseEntity<DashboardTeacherResponse> getTeacherDashboard(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(teacherDashboardUseCase.execute(userId));
    }

    /**
     * RF-93: Dashboard for the Evaluator.
     * Assigned projects and thesis plans for evaluation.
     */
    @GetMapping("/evaluator/{userId}")
    public ResponseEntity<DashboardEvaluatorResponse> getEvaluatorDashboard(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(evaluatorDashboardUseCase.execute(userId));
    }

    /**
     * Dashboard for the Dean.
     * Faculty view: procedures pending signature, resolutions and calls for applications.
     */
    @GetMapping("/dean/{userId}")
    public ResponseEntity<DashboardDeanResponse> getDeanDashboard(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(deanDashboardUseCase.execute(userId));
    }

    /**
     * Dashboard for the Student / Thesis candidate.
     * Status of their thesis plan, group, procedures and active calls.
     */
    @GetMapping("/student/{userId}")
    public ResponseEntity<DashboardStudentResponse> getStudentDashboard(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(studentDashboardUseCase.execute(userId));
    }
}