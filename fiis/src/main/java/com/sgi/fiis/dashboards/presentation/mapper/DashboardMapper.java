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
    public DashboardAdminResponse toAdminResponse(DashboardAdmin model) {
        return DashboardAdminResponse.builder()
                .totalUsers(model.getTotalUsers())
                .totalActiveUsers(model.getTotalActiveUsers())
                .totalGroups(model.getTotalGroups())
                .totalActiveGroups(model.getTotalActiveGroups())
                .totalProjects(model.getTotalProjects())
                .activeProjects(model.getActiveProjects())
                .pendingProcedures(model.getPendingProcedures())
                .issuedResolutions(model.getIssuedResolutions())
                .proceduresUnderReview(model.getProceduresUnderReview())
                .approvedProcedures(model.getApprovedProcedures())
                .rejectedProcedures(model.getRejectedProcedures())
                .alerts(mapAlerts(model.getAlerts()))
                .build();
    }

    // =========================================================================
    // DIRECTOR
    // =========================================================================
    public DashboardDirectorResponse toDirectorResponse(DashboardDirector model) {
        return DashboardDirectorResponse.builder()
                .totalProjects(model.getTotalProjects())
                .activeProjects(model.getActiveProjects())
                .submittedProjects(model.getSubmittedProjects())
                .observedProjects(model.getObservedProjects())
                .pendingReviewProcedures(model.getPendingReviewProcedures())
                .reportsNearingDeadline(model.getReportsNearingDeadline())
                .issuedResolutions(model.getIssuedResolutions())
                .openCallsForApplication(model.getOpenCallsForApplication())
                .proceduresWithCoordinator(model.getProceduresWithCoordinator())
                .proceduresWithDirector(model.getProceduresWithDirector())
                .proceduresWithDean(model.getProceduresWithDean())
                .completedProcedures(model.getCompletedProcedures())
                .alerts(mapAlerts(model.getAlerts()))
                .build();
    }

    // =========================================================================
    // COORDINATOR
    // =========================================================================
    public DashboardCoordinatorResponse toCoordinatorResponse(DashboardCoordinator model) {
        return DashboardCoordinatorResponse.builder()
                .groupId(model.getGroupId())
                .groupName(model.getGroupName())
                .groupCode(model.getGroupCode())
                .totalMembers(model.getTotalMembers())
                .activeMembers(model.getActiveMembers())
                .totalGroupProjects(model.getTotalGroupProjects())
                .activeGroupProjects(model.getActiveGroupProjects())
                .pendingGroupProcedures(model.getPendingGroupProcedures())
                .groupProgressReports(model.getGroupProgressReports())
                .groupThesisPlans(model.getGroupThesisPlans())
                .submittedProcedures(model.getSubmittedProcedures())
                .proceduresUnderReview(model.getProceduresUnderReview())
                .approvedProcedures(model.getApprovedProcedures())
                .observedProcedures(model.getObservedProcedures())
                .alerts(mapAlerts(model.getAlerts()))
                .build();
    }

    // =========================================================================
    // TEACHER
    // =========================================================================
    public DashboardTeacherResponse toTeacherResponse(DashboardTeacher model) {
        return DashboardTeacherResponse.builder()
                .groupId(model.getGroupId())
                .groupName(model.getGroupName())
                .groupCode(model.getGroupCode())
                .projectsAsLead(model.getProjectsAsLead())
                .projectsAsMember(model.getProjectsAsMember())
                .pendingProcedures(model.getPendingProcedures())
                .pendingProgressReports(model.getPendingProgressReports())
                .uploadedDocuments(model.getUploadedDocuments())
                .receivedResolutions(model.getReceivedResolutions())
                .submittedProjects(model.getSubmittedProjects())
                .approvedProjects(model.getApprovedProjects())
                .projectsInExecution(model.getProjectsInExecution())
                .completedProjects(model.getCompletedProjects())
                .alerts(mapAlerts(model.getAlerts()))
                .build();
    }

    // =========================================================================
    // EVALUATOR
    // =========================================================================
    public DashboardEvaluatorResponse toEvaluatorResponse(DashboardEvaluator model) {
        return DashboardEvaluatorResponse.builder()
                .assignedEvaluations(model.getAssignedEvaluations())
                .pendingEvaluations(model.getPendingEvaluations())
                .completedEvaluations(model.getCompletedEvaluations())
                .assignedProjects(model.getAssignedProjects())
                .assignedThesisPlans(model.getAssignedThesisPlans())
                .approvedEvaluations(model.getApprovedEvaluations())
                .rejectedEvaluations(model.getRejectedEvaluations())
                .evaluationsWithObservations(model.getEvaluationsWithObservations())
                .alerts(mapAlerts(model.getAlerts()))
                .build();
    }

    // =========================================================================
    // DEAN
    // =========================================================================
    public DashboardDeanResponse toDeanResponse(DashboardDean model) {
        return DashboardDeanResponse.builder()
                .totalFacultyProjects(model.getTotalFacultyProjects())
                .activeProjects(model.getActiveProjects())
                .pendingSignatureProcedures(model.getPendingSignatureProcedures())
                .issuedResolutions(model.getIssuedResolutions())
                .activeCallsForApplication(model.getActiveCallsForApplication())
                .totalActiveGroups(model.getTotalActiveGroups())
                .waitingProcedures(model.getWaitingProcedures())
                .approvedProceduresThisMonth(model.getApprovedProceduresThisMonth())
                .rejectedProceduresThisMonth(model.getRejectedProceduresThisMonth())
                .alerts(mapAlerts(model.getAlerts()))
                .build();
    }

    // =========================================================================
    // STUDENT
    // =========================================================================
    public DashboardStudentResponse toStudentResponse(DashboardStudent model) {
        return DashboardStudentResponse.builder()
                .submittedThesisPlans(model.getSubmittedThesisPlans())
                .currentPlanStatus(model.getCurrentPlanStatus())
                .pendingProcedures(model.getPendingProcedures())
                .uploadedDocuments(model.getUploadedDocuments())
                .openCallsForApplication(model.getOpenCallsForApplication())
                .groupName(model.getGroupName())
                .groupCode(model.getGroupCode())
                .alerts(mapAlerts(model.getAlerts()))
                .build();
    }

    // =========================================================================
    // ALERTS
    // =========================================================================
    private List<AlertItemResponse> mapAlerts(List<AlertItem> alerts) {
        if (alerts == null) {
            return List.of();
        }

        return alerts.stream()
                .map(alert -> AlertItemResponse.builder()
                        .type(alert.getType())
                        .title(alert.getTitle())
                        .description(alert.getDescription())
                        .build())
                .toList();
    }
}