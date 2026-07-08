package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardAdminResponse {

    private int totalUsers;
    private int totalActiveUsers;
    private int totalGroups;
    private int totalActiveGroups;
    private int totalProjects;
    private int activeProjects;
    private int pendingProcedures;
    private int issuedResolutions;

    private int proceduresUnderReview;
    private int approvedProcedures;
    private int rejectedProcedures;

    private List<AlertItemResponse> alerts;
}