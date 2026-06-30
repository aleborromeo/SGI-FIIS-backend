package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDirector {

    private int totalProjects;
    private int activeProjects;
    private int submittedProjects;
    private int observedProjects;
    private int pendingReviewProcedures;
    private int reportsNearingDeadline;
    private int issuedResolutions;
    private int openCallsForApplication;

    private int proceduresWithCoordinator;
    private int proceduresWithDirector;
    private int proceduresWithDean;
    private int completedProcedures;

    private List<AlertItem> alerts;
}