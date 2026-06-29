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
@DisplayName("DashboardRepositoryAdapter Unit Tests")
class DashboardRepositoryAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private DashboardRepositoryAdapter repository;

    @BeforeEach
    void setUp() {
        repository = new DashboardRepositoryAdapter(jdbcTemplate);
    }

    private void mockAllCounts(int value) {
        lenient()
                .when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(value);
    }

    @Test
    @DisplayName("Should get admin dashboard with metrics and alerts")
    void getAdminDashboard_shouldReturnMetricsAndAlerts() {
        mockAllCounts(1);

        DashboardAdmin result = repository.getAdminDashboard(1);

        assertNotNull(result);
        assertEquals(1, result.getTotalUsers());
        assertEquals(1, result.getTotalActiveUsers());
        assertEquals(1, result.getTotalGroups());
        assertEquals(1, result.getTotalActiveGroups());
        assertEquals(1, result.getTotalProjects());
        assertEquals(1, result.getActiveProjects());
        assertEquals(1, result.getPendingProcedures());
        assertEquals(1, result.getIssuedResolutions());
        assertEquals(1, result.getProceduresUnderReview());
        assertEquals(1, result.getApprovedProcedures());
        assertEquals(1, result.getRejectedProcedures());
        assertNotNull(result.getAlerts());
        assertFalse(result.getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should get director dashboard with metrics and alerts")
    void getDirectorDashboard_shouldReturnMetricsAndAlerts() {
        mockAllCounts(1);

        DashboardDirector result = repository.getDirectorDashboard(1);

        assertNotNull(result);
        assertEquals(1, result.getTotalProjects());
        assertEquals(1, result.getActiveProjects());
        assertEquals(1, result.getSubmittedProjects());
        assertEquals(1, result.getObservedProjects());
        assertEquals(1, result.getPendingReviewProcedures());
        assertEquals(1, result.getReportsNearingDeadline());
        assertEquals(1, result.getIssuedResolutions());
        assertEquals(1, result.getOpenCallsForApplication());
        assertEquals(1, result.getProceduresWithCoordinator());
        assertEquals(1, result.getProceduresWithDirector());
        assertEquals(1, result.getProceduresWithDean());
        assertEquals(1, result.getCompletedProcedures());
        assertNotNull(result.getAlerts());
        assertFalse(result.getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should get coordinator dashboard when group is assigned")
    void getCoordinatorDashboard_withGroup_shouldReturnMetrics() {
        mockAllCounts(1);

        Map<String, Object> group = Map.of(
                "id_grupo", 10,
                "nombre_grupo", "Grupo de Investigación FIIS",
                "codigo_grupo", "GI-FIIS"
        );

        when(jdbcTemplate.queryForList(anyString(), eq(1)))
                .thenReturn(List.of(group));

        DashboardCoordinator result = repository.getCoordinatorDashboard(1);

        assertNotNull(result);
        assertEquals(10, result.getGroupId());
        assertEquals("Grupo de Investigación FIIS", result.getGroupName());
        assertEquals("GI-FIIS", result.getGroupCode());
        assertEquals(1, result.getTotalMembers());
        assertEquals(1, result.getActiveMembers());
        assertEquals(1, result.getTotalGroupProjects());
        assertEquals(1, result.getActiveGroupProjects());
        assertEquals(1, result.getPendingGroupProcedures());
        assertEquals(1, result.getGroupProgressReports());
        assertEquals(1, result.getGroupThesisPlans());
        assertEquals(1, result.getSubmittedProcedures());
        assertEquals(1, result.getProceduresUnderReview());
        assertEquals(1, result.getApprovedProcedures());
        assertEquals(1, result.getObservedProcedures());
        assertNotNull(result.getAlerts());
        assertFalse(result.getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should return alert when coordinator has no group assigned")
    void getCoordinatorDashboard_withoutGroup_shouldReturnAlert() {
        mockAllCounts(1);

        when(jdbcTemplate.queryForList(anyString(), eq(1)))
                .thenReturn(Collections.emptyList());

        DashboardCoordinator result = repository.getCoordinatorDashboard(1);

        assertNotNull(result);
        assertEquals("No group assigned", result.getGroupName());
        assertEquals("", result.getGroupCode());
        assertNotNull(result.getAlerts());
        assertEquals(1, result.getAlerts().size());
        assertEquals("No group assigned", result.getAlerts().get(0).getTitle());
    }

    @Test
    @DisplayName("Should get teacher dashboard with metrics and alerts")
    void getTeacherDashboard_shouldReturnMetricsAndAlerts() {
        mockAllCounts(1);

        DashboardTeacher result = repository.getTeacherDashboard(1);

        assertNotNull(result);
        assertEquals(1, result.getProjectsAsLead());
        assertEquals(1, result.getProjectsAsMember());
        assertEquals(1, result.getPendingProcedures());
        assertEquals(1, result.getPendingProgressReports());
        assertEquals(1, result.getUploadedDocuments());
        assertEquals(1, result.getReceivedResolutions());
        assertEquals(1, result.getSubmittedProjects());
        assertEquals(1, result.getApprovedProjects());
        assertEquals(1, result.getProjectsInExecution());
        assertEquals(1, result.getCompletedProjects());
        assertNotNull(result.getAlerts());
        assertFalse(result.getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should get evaluator dashboard with metrics and alerts")
    void getEvaluatorDashboard_shouldReturnMetricsAndAlerts() {
        mockAllCounts(1);

        DashboardEvaluator result = repository.getEvaluatorDashboard(1);

        assertNotNull(result);
        assertEquals(1, result.getAssignedEvaluations());
        assertEquals(1, result.getPendingEvaluations());
        assertEquals(1, result.getCompletedEvaluations());
        assertEquals(1, result.getAssignedProjects());
        assertEquals(1, result.getAssignedThesisPlans());
        assertEquals(1, result.getApprovedEvaluations());
        assertEquals(1, result.getRejectedEvaluations());
        assertEquals(1, result.getEvaluationsWithObservations());
        assertNotNull(result.getAlerts());
        assertFalse(result.getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should get dean dashboard with metrics and alerts")
    void getDeanDashboard_shouldReturnMetricsAndAlerts() {
        mockAllCounts(1);

        DashboardDean result = repository.getDeanDashboard(1);

        assertNotNull(result);
        assertEquals(1, result.getTotalFacultyProjects());
        assertEquals(1, result.getActiveProjects());
        assertEquals(1, result.getPendingSignatureProcedures());
        assertEquals(1, result.getIssuedResolutions());
        assertEquals(1, result.getActiveCallsForApplication());
        assertEquals(1, result.getTotalActiveGroups());
        assertEquals(1, result.getWaitingProcedures());
        assertEquals(1, result.getApprovedProceduresThisMonth());
        assertEquals(1, result.getRejectedProceduresThisMonth());
        assertNotNull(result.getAlerts());
        assertFalse(result.getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should get student dashboard with observed plan and group assigned")
    void getStudentDashboard_withPlanAndGroup_shouldReturnMetricsAndAlerts() {
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

        DashboardStudent result = repository.getStudentDashboard(1);

        assertNotNull(result);
        assertEquals(1, result.getSubmittedThesisPlans());
        assertEquals("OBSERVADO", result.getCurrentPlanStatus());
        assertEquals(1, result.getPendingProcedures());
        assertEquals(1, result.getUploadedDocuments());
        assertEquals(1, result.getOpenCallsForApplication());
        assertEquals("Grupo de Tesis", result.getGroupName());
        assertEquals("GT-01", result.getGroupCode());
        assertNotNull(result.getAlerts());
        assertFalse(result.getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should get student dashboard with default values without plan or group")
    void getStudentDashboard_withoutPlanOrGroup_shouldReturnDefaultValues() {
        mockAllCounts(0);

        when(jdbcTemplate.queryForList(anyString(), eq(1)))
                .thenReturn(Collections.emptyList());

        DashboardStudent result = repository.getStudentDashboard(1);

        assertNotNull(result);
        assertEquals(0, result.getSubmittedThesisPlans());
        assertEquals("NO_PLAN", result.getCurrentPlanStatus());
        assertEquals(0, result.getPendingProcedures());
        assertEquals(0, result.getUploadedDocuments());
        assertEquals(0, result.getOpenCallsForApplication());
        assertEquals("No group", result.getGroupName());
        assertEquals("", result.getGroupCode());
        assertNotNull(result.getAlerts());
        assertTrue(result.getAlerts().isEmpty());
    }
}
