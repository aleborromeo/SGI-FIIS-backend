package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardStudent {

    private int submittedThesisPlans;
    private String currentPlanStatus;
    private int pendingProcedures;
    private int uploadedDocuments;
    private int openCallsForApplication;

    private String groupName;
    private String groupCode;

    private List<AlertItem> alerts;
}
