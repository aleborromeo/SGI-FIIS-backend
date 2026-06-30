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
    @DisplayName("Should map DashboardAdmin to DashboardAdminResponse")
    void toAdminResponse_shouldMapFieldsAndAlerts() {
        AlertItem alert = AlertItem.builder()
                .type("REVIEW")
                .title("Pending procedure")
                .description("There are pending procedures")
                .build();

        DashboardAdmin model = DashboardAdmin.builder()
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

        DashboardAdminResponse response = mapper.toAdminResponse(model);

        assertEquals(10, response.getTotalUsers());
        assertEquals(8, response.getTotalActiveUsers());
        assertEquals(5, response.getTotalGroups());
        assertEquals(4, response.getTotalActiveGroups());
        assertEquals(20, response.getTotalProjects());
        assertEquals(12, response.getActiveProjects());
        assertEquals(3, response.getPendingProcedures());
        assertEquals(7, response.getIssuedResolutions());
        assertEquals(2, response.getProceduresUnderReview());
        assertEquals(9, response.getApprovedProcedures());
        assertEquals(1, response.getRejectedProcedures());

        assertEquals(1, response.getAlerts().size());
        assertEquals("REVIEW", response.getAlerts().get(0).getType());
        assertEquals("Pending procedure", response.getAlerts().get(0).getTitle());
        assertEquals("There are pending procedures", response.getAlerts().get(0).getDescription());

        DashboardAdmin noAlerts = DashboardAdmin.builder().alerts(null).build();
        assertTrue(mapper.toAdminResponse(noAlerts).getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should map DashboardDirector to DashboardDirectorResponse")
    void toDirectorResponse_shouldMapFieldsAndAlerts() {
        AlertItem alert = AlertItem.builder()
                .type("INFO")
                .title("Active call")
                .description("There is an active call")
                .build();

        DashboardDirector model = DashboardDirector.builder()
                .totalProjects(10)
                .activeProjects(6)
                .submittedProjects(3)
                .observedProjects(1)
                .pendingReviewProcedures(4)
                .reportsNearingDeadline(2)
                .issuedResolutions(9)
                .openCallsForApplication(1)
                .proceduresWithCoordinator(2)
                .proceduresWithDirector(3)
                .proceduresWithDean(4)
                .completedProcedures(5)
                .alerts(List.of(alert))
                .build();

        DashboardDirectorResponse response = mapper.toDirectorResponse(model);

        assertEquals(10, response.getTotalProjects());
        assertEquals(6, response.getActiveProjects());
        assertEquals(3, response.getSubmittedProjects());
        assertEquals(1, response.getObservedProjects());
        assertEquals(4, response.getPendingReviewProcedures());
        assertEquals(2, response.getReportsNearingDeadline());
        assertEquals(9, response.getIssuedResolutions());
        assertEquals(1, response.getOpenCallsForApplication());
        assertEquals(2, response.getProceduresWithCoordinator());
        assertEquals(3, response.getProceduresWithDirector());
        assertEquals(4, response.getProceduresWithDean());
        assertEquals(5, response.getCompletedProcedures());

        assertEquals(1, response.getAlerts().size());
        assertEquals("INFO", response.getAlerts().get(0).getType());

        DashboardDirector noAlerts = DashboardDirector.builder().alerts(null).build();
        assertTrue(mapper.toDirectorResponse(noAlerts).getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should map DashboardCoordinator to DashboardCoordinatorResponse")
    void toCoordinatorResponse_shouldMapFieldsAndAlerts() {
        AlertItem alert = AlertItem.builder()
                .type("ALERT")
                .title("Observed procedure")
                .description("There are observations")
                .build();

        DashboardCoordinator model = DashboardCoordinator.builder()
                .groupId(1)
                .groupName("Grupo FIIS")
                .groupCode("GI-FIIS")
                .totalMembers(10)
                .activeMembers(8)
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

        DashboardCoordinatorResponse response = mapper.toCoordinatorResponse(model);

        assertEquals(1, response.getGroupId());
        assertEquals("Grupo FIIS", response.getGroupName());
        assertEquals("GI-FIIS", response.getGroupCode());
        assertEquals(10, response.getTotalMembers());
        assertEquals(8, response.getActiveMembers());
        assertEquals(4, response.getTotalGroupProjects());
        assertEquals(3, response.getActiveGroupProjects());
        assertEquals(2, response.getPendingGroupProcedures());
        assertEquals(5, response.getGroupProgressReports());
        assertEquals(6, response.getGroupThesisPlans());
        assertEquals(1, response.getSubmittedProcedures());
        assertEquals(2, response.getProceduresUnderReview());
        assertEquals(3, response.getApprovedProcedures());
        assertEquals(4, response.getObservedProcedures());

        assertEquals(1, response.getAlerts().size());
        assertEquals("ALERT", response.getAlerts().get(0).getType());

        DashboardCoordinator noAlerts = DashboardCoordinator.builder().alerts(null).build();
        assertTrue(mapper.toCoordinatorResponse(noAlerts).getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should map DashboardTeacher to DashboardTeacherResponse")
    void toTeacherResponse_shouldMapFieldsAndAlerts() {
        AlertItem alert = AlertItem.builder()
                .type("REVIEW")
                .title("Pending report")
                .description("You have pending reports")
                .build();

        DashboardTeacher model = DashboardTeacher.builder()
                .projectsAsLead(2)
                .projectsAsMember(3)
                .pendingProcedures(4)
                .pendingProgressReports(5)
                .uploadedDocuments(6)
                .receivedResolutions(7)
                .submittedProjects(1)
                .approvedProjects(2)
                .projectsInExecution(3)
                .completedProjects(4)
                .alerts(List.of(alert))
                .build();

        DashboardTeacherResponse response = mapper.toTeacherResponse(model);

        assertEquals(2, response.getProjectsAsLead());
        assertEquals(3, response.getProjectsAsMember());
        assertEquals(4, response.getPendingProcedures());
        assertEquals(5, response.getPendingProgressReports());
        assertEquals(6, response.getUploadedDocuments());
        assertEquals(7, response.getReceivedResolutions());
        assertEquals(1, response.getSubmittedProjects());
        assertEquals(2, response.getApprovedProjects());
        assertEquals(3, response.getProjectsInExecution());
        assertEquals(4, response.getCompletedProjects());

        assertEquals(1, response.getAlerts().size());
        assertEquals("REVIEW", response.getAlerts().get(0).getType());

        DashboardTeacher noAlerts = DashboardTeacher.builder().alerts(null).build();
        assertTrue(mapper.toTeacherResponse(noAlerts).getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should map DashboardEvaluator to DashboardEvaluatorResponse")
    void toEvaluatorResponse_shouldMapFieldsAndAlerts() {
        AlertItem alert = AlertItem.builder()
                .type("REVIEW")
                .title("Pending evaluations")
                .description("You have pending evaluations")
                .build();

        DashboardEvaluator model = DashboardEvaluator.builder()
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

        DashboardEvaluatorResponse response = mapper.toEvaluatorResponse(model);

        assertEquals(10, response.getAssignedEvaluations());
        assertEquals(3, response.getPendingEvaluations());
        assertEquals(7, response.getCompletedEvaluations());
        assertEquals(4, response.getAssignedProjects());
        assertEquals(5, response.getAssignedThesisPlans());
        assertEquals(2, response.getApprovedEvaluations());
        assertEquals(1, response.getRejectedEvaluations());
        assertEquals(6, response.getEvaluationsWithObservations());

        assertEquals(1, response.getAlerts().size());
        assertEquals("REVIEW", response.getAlerts().get(0).getType());

        DashboardEvaluator noAlerts = DashboardEvaluator.builder().alerts(null).build();
        assertTrue(mapper.toEvaluatorResponse(noAlerts).getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should map DashboardDean to DashboardDeanResponse")
    void toDeanResponse_shouldMapFieldsAndAlerts() {
        AlertItem alert = AlertItem.builder()
                .type("INFO")
                .title("Active call")
                .description("There is an active call")
                .build();

        DashboardDean model = DashboardDean.builder()
                .totalFacultyProjects(20)
                .activeProjects(12)
                .pendingSignatureProcedures(4)
                .issuedResolutions(9)
                .activeCallsForApplication(2)
                .totalActiveGroups(6)
                .waitingProcedures(3)
                .approvedProceduresThisMonth(5)
                .rejectedProceduresThisMonth(1)
                .alerts(List.of(alert))
                .build();

        DashboardDeanResponse response = mapper.toDeanResponse(model);

        assertEquals(20, response.getTotalFacultyProjects());
        assertEquals(12, response.getActiveProjects());
        assertEquals(4, response.getPendingSignatureProcedures());
        assertEquals(9, response.getIssuedResolutions());
        assertEquals(2, response.getActiveCallsForApplication());
        assertEquals(6, response.getTotalActiveGroups());
        assertEquals(3, response.getWaitingProcedures());
        assertEquals(5, response.getApprovedProceduresThisMonth());
        assertEquals(1, response.getRejectedProceduresThisMonth());

        assertEquals(1, response.getAlerts().size());
        assertEquals("INFO", response.getAlerts().get(0).getType());

        DashboardDean noAlerts = DashboardDean.builder().alerts(null).build();
        assertTrue(mapper.toDeanResponse(noAlerts).getAlerts().isEmpty());
    }

    @Test
    @DisplayName("Should map DashboardStudent to DashboardStudentResponse")
    void toStudentResponse_shouldMapFieldsAndAlerts() {
        AlertItem alert = AlertItem.builder()
                .type("ALERT")
                .title("Observed plan")
                .description("You must address observations")
                .build();

        DashboardStudent model = DashboardStudent.builder()
                .submittedThesisPlans(1)
                .currentPlanStatus("OBSERVADO")
                .pendingProcedures(2)
                .uploadedDocuments(3)
                .openCallsForApplication(4)
                .groupName("Grupo Tesis")
                .groupCode("GT-01")
                .alerts(List.of(alert))
                .build();

        DashboardStudentResponse response = mapper.toStudentResponse(model);

        assertEquals(1, response.getSubmittedThesisPlans());
        assertEquals("OBSERVADO", response.getCurrentPlanStatus());
        assertEquals(2, response.getPendingProcedures());
        assertEquals(3, response.getUploadedDocuments());
        assertEquals(4, response.getOpenCallsForApplication());
        assertEquals("Grupo Tesis", response.getGroupName());
        assertEquals("GT-01", response.getGroupCode());

        assertEquals(1, response.getAlerts().size());
        assertEquals("ALERT", response.getAlerts().get(0).getType());

        DashboardStudent noAlerts = DashboardStudent.builder().alerts(null).build();
        assertTrue(mapper.toStudentResponse(noAlerts).getAlerts().isEmpty());
    }
}