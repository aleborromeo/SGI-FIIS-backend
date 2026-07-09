package com.sgi.fiis.dashboards.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dashboard Domain Model Unit Tests")
class DashboardDomainModelTest {

    @Test
    @DisplayName("Should build AlertItem with all fields")
    void alertItem_shouldBuildWithAllFields() {
        AlertItem alert = AlertItem.builder()
                .type("ALERT")
                .title("Observed projects")
                .description("Projects require correction")
                .build();

        assertEquals("ALERT", alert.getType());
        assertEquals("Observed projects", alert.getTitle());
        assertEquals("Projects require correction", alert.getDescription());
    }

    @Test
    @DisplayName("Should build DashboardAdmin with all fields")
    void dashboardAdmin_shouldBuildWithAllFields() {
        AlertItem alert = AlertItem.builder()
                .type("REVIEW")
                .title("Pending procedures")
                .description("There are pending procedures")
                .build();

        DashboardAdmin dashboard = DashboardAdmin.builder()
                .totalUsers(10)
                .totalActiveUsers(8)
                .totalGroups(5)
                .totalActiveGroups(4)
                .totalProjects(20)
                .activeProjects(12)
                .pendingProcedures(3)
                .issuedResolutions(7)
                .proceduresUnderReview(2)
                .approvedProcedures(9)
                .rejectedProcedures(1)
                .alerts(List.of(alert))
                .build();

        assertEquals(10, dashboard.getTotalUsers());
        assertEquals(8, dashboard.getTotalActiveUsers());
        assertEquals(5, dashboard.getTotalGroups());
        assertEquals(4, dashboard.getTotalActiveGroups());
        assertEquals(20, dashboard.getTotalProjects());
        assertEquals(12, dashboard.getActiveProjects());
        assertEquals(3, dashboard.getPendingProcedures());
        assertEquals(7, dashboard.getIssuedResolutions());
        assertEquals(2, dashboard.getProceduresUnderReview());
        assertEquals(9, dashboard.getApprovedProcedures());
        assertEquals(1, dashboard.getRejectedProcedures());
        assertEquals(1, dashboard.getAlerts().size());
        assertSame(alert, dashboard.getAlerts().get(0));
    }

    @Test
    @DisplayName("Should build DashboardDirector with all fields")
    void dashboardDirector_shouldBuildWithAllFields() {
        AlertItem alert = AlertItem.builder()
                .type("INFO")
                .title("Active call")
                .description("There is an active call")
                .build();

        DashboardDirector dashboard = DashboardDirector.builder()
                .totalProjects(20)
                .activeProjects(12)
                .submittedProjects(5)
                .observedProjects(2)
                .pendingReviewProcedures(4)
                .reportsNearingDeadline(3)
                .issuedResolutions(9)
                .openCallsForApplication(1)
                .proceduresWithCoordinator(2)
                .proceduresWithDirector(3)
                .proceduresWithDean(4)
                .completedProcedures(5)
                .alerts(List.of(alert))
                .build();

        assertEquals(20, dashboard.getTotalProjects());
        assertEquals(12, dashboard.getActiveProjects());
        assertEquals(5, dashboard.getSubmittedProjects());
        assertEquals(2, dashboard.getObservedProjects());
        assertEquals(4, dashboard.getPendingReviewProcedures());
        assertEquals(3, dashboard.getReportsNearingDeadline());
        assertEquals(9, dashboard.getIssuedResolutions());
        assertEquals(1, dashboard.getOpenCallsForApplication());
        assertEquals(2, dashboard.getProceduresWithCoordinator());
        assertEquals(3, dashboard.getProceduresWithDirector());
        assertEquals(4, dashboard.getProceduresWithDean());
        assertEquals(5, dashboard.getCompletedProcedures());
        assertEquals(1, dashboard.getAlerts().size());
        assertSame(alert, dashboard.getAlerts().get(0));
    }

    @Test
    @DisplayName("Should build DashboardCoordinator with all fields")
    void dashboardCoordinator_shouldBuildWithAllFields() {
        AlertItem alert = AlertItem.builder()
                .type("ALERT")
                .title("Observed procedures")
                .description("Procedures require corrections")
                .build();

        DashboardCoordinator dashboard = DashboardCoordinator.builder()
                .groupId(10)
                .groupName("Grupo FIIS")
                .groupCode("GI-FIIS")
                .totalMembers(8)
                .activeMembers(7)
                .totalGroupProjects(4)
                .activeGroupProjects(3)
                .pendingGroupProcedures(2)
                .groupProgressReports(5)
                .groupThesisPlans(6)
                .submittedProcedures(1)
                .proceduresUnderReview(2)
                .approvedProcedures(3)
                .observedProcedures(4)
                .alerts(List.of(alert))
                .build();

        assertEquals(10, dashboard.getGroupId());
        assertEquals("Grupo FIIS", dashboard.getGroupName());
        assertEquals("GI-FIIS", dashboard.getGroupCode());
        assertEquals(8, dashboard.getTotalMembers());
        assertEquals(7, dashboard.getActiveMembers());
        assertEquals(4, dashboard.getTotalGroupProjects());
        assertEquals(3, dashboard.getActiveGroupProjects());
        assertEquals(2, dashboard.getPendingGroupProcedures());
        assertEquals(5, dashboard.getGroupProgressReports());
        assertEquals(6, dashboard.getGroupThesisPlans());
        assertEquals(1, dashboard.getSubmittedProcedures());
        assertEquals(2, dashboard.getProceduresUnderReview());
        assertEquals(3, dashboard.getApprovedProcedures());
        assertEquals(4, dashboard.getObservedProcedures());
        assertEquals(1, dashboard.getAlerts().size());
        assertSame(alert, dashboard.getAlerts().get(0));
    }

    @Test
    @DisplayName("Should build DashboardTeacher with all fields")
    void dashboardTeacher_shouldBuildWithAllFields() {
        AlertItem alert = AlertItem.builder()
                .type("REVIEW")
                .title("Procedures in progress")
                .description("Procedures still under review")
                .build();

        DashboardTeacher dashboard = DashboardTeacher.builder()
                .projectsAsLead(2)
                .projectsAsMember(4)
                .pendingProcedures(1)
                .pendingProgressReports(3)
                .uploadedDocuments(6)
                .receivedResolutions(5)
                .submittedProjects(1)
                .approvedProjects(2)
                .projectsInExecution(3)
                .completedProjects(4)
                .alerts(List.of(alert))
                .build();

        assertEquals(2, dashboard.getProjectsAsLead());
        assertEquals(4, dashboard.getProjectsAsMember());
        assertEquals(1, dashboard.getPendingProcedures());
        assertEquals(3, dashboard.getPendingProgressReports());
        assertEquals(6, dashboard.getUploadedDocuments());
        assertEquals(5, dashboard.getReceivedResolutions());
        assertEquals(1, dashboard.getSubmittedProjects());
        assertEquals(2, dashboard.getApprovedProjects());
        assertEquals(3, dashboard.getProjectsInExecution());
        assertEquals(4, dashboard.getCompletedProjects());
        assertEquals(1, dashboard.getAlerts().size());
        assertSame(alert, dashboard.getAlerts().get(0));
    }

    @Test
    @DisplayName("Should build DashboardEvaluator with all fields")
    void dashboardEvaluator_shouldBuildWithAllFields() {
        AlertItem alert = AlertItem.builder()
                .type("REVIEW")
                .title("Pending evaluations")
                .description("Evaluations not completed")
                .build();

        DashboardEvaluator dashboard = DashboardEvaluator.builder()
                .assignedEvaluations(10)
                .pendingEvaluations(3)
                .completedEvaluations(7)
                .assignedProjects(4)
                .assignedThesisPlans(5)
                .approvedEvaluations(2)
                .rejectedEvaluations(1)
                .evaluationsWithObservations(6)
                .alerts(List.of(alert))
                .build();

        assertEquals(10, dashboard.getAssignedEvaluations());
        assertEquals(3, dashboard.getPendingEvaluations());
        assertEquals(7, dashboard.getCompletedEvaluations());
        assertEquals(4, dashboard.getAssignedProjects());
        assertEquals(5, dashboard.getAssignedThesisPlans());
        assertEquals(2, dashboard.getApprovedEvaluations());
        assertEquals(1, dashboard.getRejectedEvaluations());
        assertEquals(6, dashboard.getEvaluationsWithObservations());
        assertEquals(1, dashboard.getAlerts().size());
        assertSame(alert, dashboard.getAlerts().get(0));
    }

    @Test
    @DisplayName("Should build DashboardDean with all fields")
    void dashboardDean_shouldBuildWithAllFields() {
        AlertItem alert = AlertItem.builder()
                .type("INFO")
                .title("Active call")
                .description("There are open calls")
                .build();

        DashboardDean dashboard = DashboardDean.builder()
                .totalFacultyProjects(30)
                .activeProjects(15)
                .pendingSignatureProcedures(4)
                .issuedResolutions(8)
                .activeCallsForApplication(2)
                .totalActiveGroups(6)
                .waitingProcedures(3)
                .approvedProceduresThisMonth(5)
                .rejectedProceduresThisMonth(1)
                .alerts(List.of(alert))
                .build();

        assertEquals(30, dashboard.getTotalFacultyProjects());
        assertEquals(15, dashboard.getActiveProjects());
        assertEquals(4, dashboard.getPendingSignatureProcedures());
        assertEquals(8, dashboard.getIssuedResolutions());
        assertEquals(2, dashboard.getActiveCallsForApplication());
        assertEquals(6, dashboard.getTotalActiveGroups());
        assertEquals(3, dashboard.getWaitingProcedures());
        assertEquals(5, dashboard.getApprovedProceduresThisMonth());
        assertEquals(1, dashboard.getRejectedProceduresThisMonth());
        assertEquals(1, dashboard.getAlerts().size());
        assertSame(alert, dashboard.getAlerts().get(0));
    }

    @Test
    @DisplayName("Should build DashboardStudent with all fields")
    void dashboardStudent_shouldBuildWithAllFields() {
        AlertItem alert = AlertItem.builder()
                .type("ALERT")
                .title("Thesis plan observed")
                .description("Plan has observations")
                .build();

        DashboardStudent dashboard = DashboardStudent.builder()
                .submittedThesisPlans(1)
                .currentPlanStatus("OBSERVADO")
                .pendingProcedures(2)
                .uploadedDocuments(3)
                .openCallsForApplication(4)
                .groupName("Grupo Tesis")
                .groupCode("GT-01")
                .alerts(List.of(alert))
                .build();

        assertEquals(1, dashboard.getSubmittedThesisPlans());
        assertEquals("OBSERVADO", dashboard.getCurrentPlanStatus());
        assertEquals(2, dashboard.getPendingProcedures());
        assertEquals(3, dashboard.getUploadedDocuments());
        assertEquals(4, dashboard.getOpenCallsForApplication());
        assertEquals("Grupo Tesis", dashboard.getGroupName());
        assertEquals("GT-01", dashboard.getGroupCode());
        assertEquals(1, dashboard.getAlerts().size());
        assertSame(alert, dashboard.getAlerts().get(0));
    }
}