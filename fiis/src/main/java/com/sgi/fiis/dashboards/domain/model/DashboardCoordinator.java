package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardCoordinator {

    private int groupId;
    private String groupName;
    private String groupCode;

    private int totalMembers;
    private int activeMembers;
    private int totalGroupProjects;
    private int activeGroupProjects;
    private int pendingGroupProcedures;
    private int groupProgressReports;
    private int groupThesisPlans;

    private int submittedProcedures;
    private int proceduresUnderReview;
    private int approvedProcedures;
    private int observedProcedures;

    private List<AlertItem> alerts;
}
