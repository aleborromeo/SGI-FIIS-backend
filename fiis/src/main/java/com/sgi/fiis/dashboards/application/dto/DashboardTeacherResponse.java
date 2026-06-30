package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardTeacherResponse {

    private int projectsAsLead;
    private int projectsAsMember;
    private int pendingProcedures;
    private int pendingProgressReports;
    private int uploadedDocuments;
    private int receivedResolutions;

    private int submittedProjects;
    private int approvedProjects;
    private int projectsInExecution;
    private int completedProjects;

    private List<AlertItemResponse> alerts;
}
