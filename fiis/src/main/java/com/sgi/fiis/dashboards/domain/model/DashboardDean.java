package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDean {

    private int totalFacultyProjects;
    private int activeProjects;
    private int pendingSignatureProcedures;
    private int issuedResolutions;
    private int activeCallsForApplication;
    private int totalActiveGroups;

    private int waitingProcedures;
    private int approvedProceduresThisMonth;
    private int rejectedProceduresThisMonth;

    private List<AlertItem> alerts;
}
