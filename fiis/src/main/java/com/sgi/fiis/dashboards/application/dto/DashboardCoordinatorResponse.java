package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardCoordinatorResponse {

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

    private List<AlertItemResponse> alerts;
}
