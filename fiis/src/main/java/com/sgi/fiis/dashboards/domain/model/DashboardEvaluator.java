package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardEvaluator {

    private int assignedEvaluations;
    private int pendingEvaluations;
    private int completedEvaluations;
    private int assignedProjects;
    private int assignedThesisPlans;

    private int approvedEvaluations;
    private int rejectedEvaluations;
    private int evaluationsWithObservations;

    private List<AlertItem> alerts;
}
