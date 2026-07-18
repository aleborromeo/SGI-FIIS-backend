package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardEvaluatorResponse {

    private int assignedEvaluations;
    private int pendingEvaluations;
    private int completedEvaluations;
    private int assignedProjects;
    private int assignedThesisPlans;
    private String groupName;
    private String groupCode;

    private int approvedEvaluations;
    private int rejectedEvaluations;
    private int evaluationsWithObservations;

    private List<AlertItemResponse> alerts;
}
