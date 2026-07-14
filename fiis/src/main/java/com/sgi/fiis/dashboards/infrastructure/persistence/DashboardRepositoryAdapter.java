package com.sgi.fiis.dashboards.infrastructure.persistence;

import com.sgi.fiis.dashboards.domain.model.*;
import com.sgi.fiis.dashboards.domain.port.DashboardRepositoryPort;
import com.sgi.fiis.dashboards.infrastructure.i18n.DashboardMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardRepositoryAdapter implements DashboardRepositoryPort {

    private static final String ALERT_TYPE = "ALERT";
    private static final String REVIEW_TYPE = "REVIEW";
    private static final String INFO_TYPE = "INFO";

    private static final String SQL_COUNT_PROJECTS = "SELECT COUNT(*) FROM proyectos";
    private static final String SQL_COUNT_PROJECTS_IN_EXECUTION =
            "SELECT COUNT(*) FROM proyectos WHERE estado_proyecto = 'EN_EJECUCION'";
    private static final String SQL_COUNT_RESOLUTIONS = "SELECT COUNT(*) FROM resoluciones";
    private static final String SQL_COUNT_OPEN_CALLS =
            "SELECT COUNT(*) FROM convocatorias WHERE estado = 'ABIERTA' AND fecha_fin >= CURRENT_DATE";

    private static final String SQL_COUNT_GROUP_PROCEDURES =
            "SELECT COUNT(*) FROM tramites WHERE id_grupo = ";
    private static final String SQL_COUNT_PROJECTS_BY_LEAD =
            "SELECT COUNT(*) FROM proyectos WHERE id_responsable = ";
    private static final String SQL_COUNT_EVALUATIONS_BY_EVALUATOR =
            "SELECT COUNT(*) FROM evaluaciones WHERE id_evaluador = ";

    private static final String FILTER_ACTIVE = " AND es_activo = TRUE";
    private static final String FILTER_PROCEDURE_NOT_CLOSED =
            " AND estado_actual NOT IN ('APROBADO_CON_RESOLUCION','RECHAZADO','FINALIZADO')";
    private static final String ALERT_ACTIVE_CALL_TITLE = "dashboard.alert.active-call.title";

    private final JdbcTemplate jdbcTemplate;
    private final DashboardMessageService messages;

    // =========================================================================
    // ADMIN (RF-88)
    // =========================================================================
    @Override
    public DashboardAdmin getAdminDashboard(Integer userId) {

        int totalUsers = count("SELECT COUNT(*) FROM usuarios");
        int totalActiveUsers = count("SELECT COUNT(*) FROM usuarios WHERE es_activo = TRUE");
        int totalGroups = count("SELECT COUNT(*) FROM grupos_investigacion");
        int totalActiveGroups = count("SELECT COUNT(*) FROM grupos_investigacion WHERE es_activo = TRUE");
        int totalProjects = count(SQL_COUNT_PROJECTS);
        int activeProjects = count(SQL_COUNT_PROJECTS_IN_EXECUTION);
        int pendingProcedures = count("SELECT COUNT(*) FROM tramites WHERE estado_actual NOT IN ('APROBADO_CON_RESOLUCION','RECHAZADO','FINALIZADO')");
        int issuedResolutions = count(SQL_COUNT_RESOLUTIONS);
        int proceduresUnderReview = count("SELECT COUNT(*) FROM tramites WHERE estado_actual = 'REGISTRADO'");
        int approvedProcedures = count("SELECT COUNT(*) FROM tramites WHERE estado_actual = 'APROBADO_CON_RESOLUCION'");
        int rejectedProcedures = count("SELECT COUNT(*) FROM tramites WHERE estado_actual = 'RECHAZADO'");

        List<AlertItem> alerts = new ArrayList<>();

        int observedProjects = count("SELECT COUNT(*) FROM proyectos WHERE estado_proyecto = 'OBSERVADO'");
        if (observedProjects > 0) {
            alerts.add(AlertItem.builder()
                    .type(ALERT_TYPE)
                    .title(messages.get("dashboard.alert.observed-projects.title"))
                    .description(messages.get("dashboard.alert.observed-projects.description", observedProjects))
                    .build());
        }

        if (pendingProcedures > 0) {
            alerts.add(AlertItem.builder()
                    .type(REVIEW_TYPE)
                    .title(messages.get("dashboard.alert.pending-procedures.title"))
                    .description(messages.get("dashboard.alert.pending-procedures.description", pendingProcedures))
                    .build());
        }

        int openCalls = count(SQL_COUNT_OPEN_CALLS);
        if (openCalls > 0) {
            alerts.add(AlertItem.builder()
                    .type(INFO_TYPE)
                    .title(messages.get(ALERT_ACTIVE_CALL_TITLE))
                    .description(messages.get("dashboard.alert.active-call.admin.description", openCalls))
                    .build());
        }

        return DashboardAdmin.builder()
                .totalUsers(totalUsers)
                .totalActiveUsers(totalActiveUsers)
                .totalGroups(totalGroups)
                .totalActiveGroups(totalActiveGroups)
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .pendingProcedures(pendingProcedures)
                .issuedResolutions(issuedResolutions)
                .proceduresUnderReview(proceduresUnderReview)
                .approvedProcedures(approvedProcedures)
                .rejectedProcedures(rejectedProcedures)
                .alerts(alerts)
                .build();
    }

    // =========================================================================
    // RESEARCH DIRECTOR (RF-89)
    // =========================================================================
    @Override
    public DashboardDirector getDirectorDashboard(Integer userId) {

        int totalProjects = count(SQL_COUNT_PROJECTS);
        int activeProjects = count(SQL_COUNT_PROJECTS_IN_EXECUTION);
        int submittedProjects = count("SELECT COUNT(*) FROM proyectos WHERE estado_proyecto = 'POSTULADO'");
        int observedProjects = count("SELECT COUNT(*) FROM proyectos WHERE estado_proyecto = 'OBSERVADO'");
        int pendingReviewProcedures = count(
                "SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'DIRECTOR_INVESTIGACION'"
                        + FILTER_PROCEDURE_NOT_CLOSED);
        int reportsNearingDeadline = count(
                "SELECT COUNT(*) FROM informes_avance WHERE estado_informe = 'PENDIENTE'");
        int issuedResolutions = count(SQL_COUNT_RESOLUTIONS);
        int openCalls = count(SQL_COUNT_OPEN_CALLS);

        int proceduresWithCoordinator = count("SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'COORDINADOR_GRUPO'");
        int proceduresWithDirector = count("SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'DIRECTOR_INVESTIGACION'");
        int proceduresWithDean = count("SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'DECANO'");
        int completedProcedures = count("SELECT COUNT(*) FROM tramites WHERE estado_actual IN ('APROBADO_CON_RESOLUCION','RECHAZADO')");

        List<AlertItem> alerts = new ArrayList<>();

        if (pendingReviewProcedures > 0) {
            alerts.add(AlertItem.builder()
                    .type(REVIEW_TYPE)
                    .title(messages.get("dashboard.alert.pending-review-procedures.title"))
                    .description(messages.get("dashboard.alert.pending-review-procedures.description", pendingReviewProcedures))
                    .build());
        }

        if (reportsNearingDeadline > 0) {
            alerts.add(AlertItem.builder()
                    .type(ALERT_TYPE)
                    .title(messages.get("dashboard.alert.pending-progress-reports.title"))
                    .description(messages.get("dashboard.alert.pending-progress-reports.description", reportsNearingDeadline))
                    .build());
        }

        if (openCalls > 0) {
            alerts.add(AlertItem.builder()
                    .type(INFO_TYPE)
                    .title(messages.get(ALERT_ACTIVE_CALL_TITLE))
                    .description(messages.get("dashboard.alert.active-call.director.description"))
                    .build());
        }

        return DashboardDirector.builder()
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .submittedProjects(submittedProjects)
                .observedProjects(observedProjects)
                .pendingReviewProcedures(pendingReviewProcedures)
                .reportsNearingDeadline(reportsNearingDeadline)
                .issuedResolutions(issuedResolutions)
                .openCallsForApplication(openCalls)
                .proceduresWithCoordinator(proceduresWithCoordinator)
                .proceduresWithDirector(proceduresWithDirector)
                .proceduresWithDean(proceduresWithDean)
                .completedProcedures(completedProcedures)
                .alerts(alerts)
                .build();
    }

    // =========================================================================
    // GROUP COORDINATOR (RF-90, RF-91)
    // =========================================================================
    @Override
    public DashboardCoordinator getCoordinatorDashboard(Integer userId) {

        String sqlGroup = """
                SELECT g.id_grupo, g.nombre_grupo, g.codigo_grupo
                FROM grupos_investigacion g
                WHERE g.id_coordinador_actual = ?
                AND g.es_activo = TRUE
                LIMIT 1
                """;

        Integer groupId = null;
        String groupName = null;
        String groupCode = null;

        List<java.util.Map<String, Object>> groupResult = jdbcTemplate.queryForList(sqlGroup, userId);
        if (!groupResult.isEmpty()) {
            java.util.Map<String, Object> row = groupResult.get(0);
            Object idVal = row.get("id_grupo");
            groupId = idVal instanceof Number number ? number.intValue() : null;
            groupName = (String) row.get("nombre_grupo");
            groupCode = (String) row.get("codigo_grupo");
        }

        if (groupId == null) {
            return DashboardCoordinator.builder()
                    .groupName(messages.get("dashboard.default.no-group-assigned"))
                    .groupCode("")
                    .alerts(List.of(AlertItem.builder()
                            .type(ALERT_TYPE)
                            .title(messages.get("dashboard.alert.no-group-assigned.title"))
                            .description(messages.get("dashboard.alert.no-group-assigned.description"))
                            .build()))
                    .build();
        }

        int totalMembers = count("SELECT COUNT(*) FROM membresias_grupo WHERE id_grupo = " + groupId);
        int activeMembers = count("SELECT COUNT(*) FROM membresias_grupo WHERE id_grupo = " + groupId + FILTER_ACTIVE);
        int totalGroupProjects = count("SELECT COUNT(*) FROM proyectos WHERE id_grupo = " + groupId);
        int activeGroupProjects = count(
                "SELECT COUNT(*) FROM proyectos WHERE id_grupo = " + groupId + " AND estado_proyecto = 'EN_EJECUCION'");
        int pendingGroupProcedures = count(SQL_COUNT_GROUP_PROCEDURES + groupId + FILTER_PROCEDURE_NOT_CLOSED);
        int groupProgressReports = count(
                "SELECT COUNT(*) FROM informes_avance ia INNER JOIN proyectos p ON ia.id_proyecto = p.id_proyecto WHERE p.id_grupo = "
                        + groupId);
        int groupThesisPlans = count("SELECT COUNT(*) FROM planes_tesis WHERE id_grupo = " + groupId);

        int submittedProcedures = count(SQL_COUNT_GROUP_PROCEDURES + groupId + " AND estado_actual = 'REGISTRADO'");
        int proceduresUnderReview = count(SQL_COUNT_GROUP_PROCEDURES + groupId + " AND estado_actual = 'PENDIENTE_COORDINADOR'");
        int approvedProcedures = count(SQL_COUNT_GROUP_PROCEDURES + groupId + " AND estado_actual = 'APROBADO_CON_RESOLUCION'");
        int observedProcedures = count(SQL_COUNT_GROUP_PROCEDURES + groupId + " AND estado_actual = 'OBSERVADO'");

        List<AlertItem> alerts = new ArrayList<>();

        if (pendingGroupProcedures > 0) {
            alerts.add(AlertItem.builder()
                    .type(REVIEW_TYPE)
                    .title(messages.get("dashboard.alert.pending-group-procedures.title"))
                    .description(messages.get("dashboard.alert.pending-group-procedures.description", pendingGroupProcedures))
                    .build());
        }

        if (observedProcedures > 0) {
            alerts.add(AlertItem.builder()
                    .type(ALERT_TYPE)
                    .title(messages.get("dashboard.alert.observed-procedures.title"))
                    .description(messages.get("dashboard.alert.observed-procedures.description", observedProcedures))
                    .build());
        }

        return DashboardCoordinator.builder()
                .groupId(groupId)
                .groupName(groupName)
                .groupCode(groupCode)
                .totalMembers(totalMembers)
                .activeMembers(activeMembers)
                .totalGroupProjects(totalGroupProjects)
                .activeGroupProjects(activeGroupProjects)
                .pendingGroupProcedures(pendingGroupProcedures)
                .groupProgressReports(groupProgressReports)
                .groupThesisPlans(groupThesisPlans)
                .submittedProcedures(submittedProcedures)
                .proceduresUnderReview(proceduresUnderReview)
                .approvedProcedures(approvedProcedures)
                .observedProcedures(observedProcedures)
                .alerts(alerts)
                .build();
    }

    // =========================================================================
    // RESEARCH TEACHER (RF-92)
    // =========================================================================
    @Override
    public DashboardTeacher getTeacherDashboard(Integer userId) {

        int projectsAsLead = count(SQL_COUNT_PROJECTS_BY_LEAD + userId);
        int projectsAsMember = count(
                "SELECT COUNT(*) FROM miembros_proyecto WHERE id_usuario = " + userId);
        int pendingProcedures = count(
                "SELECT COUNT(*) FROM tramites WHERE id_solicitante = " + userId + FILTER_PROCEDURE_NOT_CLOSED);
        int pendingProgressReports = count(
                "SELECT COUNT(*) FROM informes_avance ia INNER JOIN proyectos p ON ia.id_proyecto = p.id_proyecto WHERE p.id_responsable = "
                        + userId + " AND ia.estado_informe = 'PENDIENTE'");
        int uploadedDocuments = count(
                "SELECT COUNT(*) FROM documentos WHERE id_usuario_subio = " + userId + FILTER_ACTIVE);
        int receivedResolutions = count(
                "SELECT COUNT(*) FROM resoluciones r INNER JOIN tramites t ON r.id_tramite = t.id_tramite WHERE t.id_solicitante = "
                        + userId);

        int submittedProjects = count(SQL_COUNT_PROJECTS_BY_LEAD + userId + " AND estado_proyecto = 'POSTULADO'");
        int approvedProjects = count(SQL_COUNT_PROJECTS_BY_LEAD + userId + " AND estado_proyecto = 'APROBADO'");
        int projectsInExecution = count(SQL_COUNT_PROJECTS_BY_LEAD + userId + " AND estado_proyecto = 'EN_EJECUCION'");
        int completedProjects = count(SQL_COUNT_PROJECTS_BY_LEAD + userId + " AND estado_proyecto = 'FINALIZADO'");

        Integer groupId = null;
        String groupName = null;
        String groupCode = null;
        List<java.util.Map<String, Object>> groupResult = jdbcTemplate.queryForList(
                """
                SELECT g.id_grupo, g.nombre_grupo, g.codigo_grupo
                FROM membresias_grupo m
                INNER JOIN grupos_investigacion g ON m.id_grupo = g.id_grupo
                WHERE m.id_usuario = ? AND m.es_activo = TRUE
                LIMIT 1
                """, userId);
        if (!groupResult.isEmpty()) {
            groupId = ((Number) groupResult.get(0).get("id_grupo")).intValue();
            groupName = (String) groupResult.get(0).get("nombre_grupo");
            groupCode = (String) groupResult.get(0).get("codigo_grupo");
        }
        if (groupId == null) {
            List<java.util.Map<String, Object>> coordResult = jdbcTemplate.queryForList(
                    """
                    SELECT g.id_grupo, g.nombre_grupo, g.codigo_grupo
                    FROM grupos_investigacion g
                    WHERE g.id_coordinador_actual = ? AND g.es_activo = TRUE
                    LIMIT 1
                    """, userId);
            if (!coordResult.isEmpty()) {
                groupId = ((Number) coordResult.get(0).get("id_grupo")).intValue();
                groupName = (String) coordResult.get(0).get("nombre_grupo");
                groupCode = (String) coordResult.get(0).get("codigo_grupo");
            }
        }

        List<AlertItem> alerts = new ArrayList<>();

        if (pendingProcedures > 0) {
            alerts.add(AlertItem.builder()
                    .type(REVIEW_TYPE)
                    .title(messages.get("dashboard.alert.procedures-in-progress.title"))
                    .description(messages.get("dashboard.alert.procedures-in-progress.teacher.description", pendingProcedures))
                    .build());
        }

        if (pendingProgressReports > 0) {
            alerts.add(AlertItem.builder()
                    .type(ALERT_TYPE)
                    .title(messages.get("dashboard.alert.pending-progress-reports.title"))
                    .description(messages.get("dashboard.alert.pending-progress-reports.description", pendingProgressReports))
                    .build());
        }

        int openCalls = count(SQL_COUNT_OPEN_CALLS);
        if (openCalls > 0) {
            alerts.add(AlertItem.builder()
                    .type(INFO_TYPE)
                    .title(messages.get(ALERT_ACTIVE_CALL_TITLE))
                    .description(messages.get("dashboard.alert.active-call.general.description", openCalls))
                    .build());
        }

        return DashboardTeacher.builder()
                .groupId(groupId)
                .groupName(groupName != null ? groupName : messages.get("dashboard.default.no-group"))
                .groupCode(groupCode != null ? groupCode : "")
                .projectsAsLead(projectsAsLead)
                .projectsAsMember(projectsAsMember)
                .pendingProcedures(pendingProcedures)
                .pendingProgressReports(pendingProgressReports)
                .uploadedDocuments(uploadedDocuments)
                .receivedResolutions(receivedResolutions)
                .submittedProjects(submittedProjects)
                .approvedProjects(approvedProjects)
                .projectsInExecution(projectsInExecution)
                .completedProjects(completedProjects)
                .alerts(alerts)
                .build();
    }

    // =========================================================================
    // EVALUATOR (RF-93)
    // =========================================================================
    @Override
    public DashboardEvaluator getEvaluatorDashboard(Integer userId) {

        int assignedEvaluations = count(SQL_COUNT_EVALUATIONS_BY_EVALUATOR + userId);
        int pendingEvaluations = count(SQL_COUNT_EVALUATIONS_BY_EVALUATOR + userId + " AND fecha_evaluacion IS NULL");
        int completedEvaluations = count(SQL_COUNT_EVALUATIONS_BY_EVALUATOR + userId + " AND fecha_evaluacion IS NOT NULL");
        int assignedProjects = count(SQL_COUNT_EVALUATIONS_BY_EVALUATOR + userId + " AND id_proyecto IS NOT NULL");
        int assignedThesisPlans = count(SQL_COUNT_EVALUATIONS_BY_EVALUATOR + userId + " AND id_plan_tesis IS NOT NULL");

        int approvedEvaluations = count(SQL_COUNT_EVALUATIONS_BY_EVALUATOR + userId + " AND resultado = 'APROBADO'");
        int rejectedEvaluations = count(SQL_COUNT_EVALUATIONS_BY_EVALUATOR + userId + " AND resultado = 'RECHAZADO'");
        int evaluationsWithObservations = count(SQL_COUNT_EVALUATIONS_BY_EVALUATOR + userId + " AND resultado = 'CON_OBSERVACIONES'");

        List<AlertItem> alerts = new ArrayList<>();

        if (pendingEvaluations > 0) {
            alerts.add(AlertItem.builder()
                    .type(REVIEW_TYPE)
                    .title(messages.get("dashboard.alert.pending-evaluations.title"))
                    .description(messages.get("dashboard.alert.pending-evaluations.description", pendingEvaluations))
                    .build());
        }

        return DashboardEvaluator.builder()
                .assignedEvaluations(assignedEvaluations)
                .pendingEvaluations(pendingEvaluations)
                .completedEvaluations(completedEvaluations)
                .assignedProjects(assignedProjects)
                .assignedThesisPlans(assignedThesisPlans)
                .approvedEvaluations(approvedEvaluations)
                .rejectedEvaluations(rejectedEvaluations)
                .evaluationsWithObservations(evaluationsWithObservations)
                .alerts(alerts)
                .build();
    }

    // =========================================================================
    // DEAN
    // =========================================================================
    @Override
    public DashboardDean getDeanDashboard(Integer userId) {

        int totalFacultyProjects = count(SQL_COUNT_PROJECTS);
        int activeProjects = count(SQL_COUNT_PROJECTS_IN_EXECUTION);
        int pendingSignatureProcedures = count(
                "SELECT COUNT(*) FROM tramites WHERE rol_revisor_actual = 'DECANO'" + FILTER_PROCEDURE_NOT_CLOSED);
        int issuedResolutions = count(SQL_COUNT_RESOLUTIONS);
        int activeCallsForApplication = count(SQL_COUNT_OPEN_CALLS);
        int totalActiveGroups = count("SELECT COUNT(*) FROM grupos_investigacion WHERE es_activo = TRUE");

        int waitingProcedures = pendingSignatureProcedures;

        int approvedProceduresThisMonth = count(
                "SELECT COUNT(*) FROM tramites WHERE estado_actual = 'APROBADO_CON_RESOLUCION' AND DATE_TRUNC('month', fecha_actualizacion) = DATE_TRUNC('month', CURRENT_DATE)");
        int rejectedProceduresThisMonth = count(
                "SELECT COUNT(*) FROM tramites WHERE estado_actual = 'RECHAZADO' AND DATE_TRUNC('month', fecha_actualizacion) = DATE_TRUNC('month', CURRENT_DATE)");

        List<AlertItem> alerts = new ArrayList<>();

        if (pendingSignatureProcedures > 0) {
            alerts.add(AlertItem.builder()
                    .type(REVIEW_TYPE)
                    .title(messages.get("dashboard.alert.pending-signature-procedures.title"))
                    .description(messages.get("dashboard.alert.pending-signature-procedures.description", pendingSignatureProcedures))
                    .build());
        }

        if (activeCallsForApplication > 0) {
            alerts.add(AlertItem.builder()
                    .type(INFO_TYPE)
                    .title(messages.get(ALERT_ACTIVE_CALL_TITLE))
                    .description(messages.get("dashboard.alert.active-call.faculty.description", activeCallsForApplication))
                    .build());
        }

        return DashboardDean.builder()
                .totalFacultyProjects(totalFacultyProjects)
                .activeProjects(activeProjects)
                .pendingSignatureProcedures(pendingSignatureProcedures)
                .issuedResolutions(issuedResolutions)
                .activeCallsForApplication(activeCallsForApplication)
                .totalActiveGroups(totalActiveGroups)
                .waitingProcedures(waitingProcedures)
                .approvedProceduresThisMonth(approvedProceduresThisMonth)
                .rejectedProceduresThisMonth(rejectedProceduresThisMonth)
                .alerts(alerts)
                .build();
    }

    // =========================================================================
    // STUDENT / THESIS CANDIDATE
    // =========================================================================
    @Override
    public DashboardStudent getStudentDashboard(Integer userId) {

        int submittedThesisPlans = count("SELECT COUNT(*) FROM planes_tesis WHERE id_estudiante = " + userId);
        int pendingProcedures = count(
                "SELECT COUNT(*) FROM tramites WHERE id_solicitante = " + userId + FILTER_PROCEDURE_NOT_CLOSED);
        int uploadedDocuments = count(
                "SELECT COUNT(*) FROM documentos WHERE id_usuario_subio = " + userId + FILTER_ACTIVE);
        int openCalls = count(SQL_COUNT_OPEN_CALLS);

        String currentPlanStatus = null;
        List<java.util.Map<String, Object>> planResult = jdbcTemplate.queryForList(
                "SELECT estado_plan FROM planes_tesis WHERE id_estudiante = ? ORDER BY fecha_creacion DESC LIMIT 1",
                userId);
        if (!planResult.isEmpty()) {
            currentPlanStatus = (String) planResult.get(0).get("estado_plan");
        }

        String groupName = null;
        String groupCode = null;
        List<java.util.Map<String, Object>> groupResult = jdbcTemplate.queryForList(
                """
                SELECT g.nombre_grupo, g.codigo_grupo
                FROM membresias_grupo m
                INNER JOIN grupos_investigacion g ON m.id_grupo = g.id_grupo
                WHERE m.id_usuario = ? AND m.es_activo = TRUE
                LIMIT 1
                """, userId);
        if (!groupResult.isEmpty()) {
            groupName = (String) groupResult.get(0).get("nombre_grupo");
            groupCode = (String) groupResult.get(0).get("codigo_grupo");
        }

        List<AlertItem> alerts = new ArrayList<>();

        if (pendingProcedures > 0) {
            alerts.add(AlertItem.builder()
                    .type(REVIEW_TYPE)
                    .title(messages.get("dashboard.alert.procedures-in-progress.title"))
                    .description(messages.get("dashboard.alert.procedures-in-progress.student.description", pendingProcedures))
                    .build());
        }

        if ("OBSERVADO".equals(currentPlanStatus)) {
            alerts.add(AlertItem.builder()
                    .type(ALERT_TYPE)
                    .title(messages.get("dashboard.alert.thesis-plan-observed.title"))
                    .description(messages.get("dashboard.alert.thesis-plan-observed.description"))
                    .build());
        }

        if (openCalls > 0) {
            alerts.add(AlertItem.builder()
                    .type(INFO_TYPE)
                    .title(messages.get(ALERT_ACTIVE_CALL_TITLE))
                    .description(messages.get("dashboard.alert.active-call.general.description", openCalls))
                    .build());
        }

        return DashboardStudent.builder()
                .submittedThesisPlans(submittedThesisPlans)
                .currentPlanStatus(currentPlanStatus != null ? currentPlanStatus : "NO_PLAN")
                .pendingProcedures(pendingProcedures)
                .uploadedDocuments(uploadedDocuments)
                .openCallsForApplication(openCalls)
                .groupName(groupName != null ? groupName : messages.get("dashboard.default.no-group"))
                .groupCode(groupCode != null ? groupCode : "")
                .alerts(alerts)
                .build();
    }

    // =========================================================================
    // Helper: safely execute COUNT(*)
    // =========================================================================
    private int count(String sql) {
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        return result != null ? result : 0;
    }
}