package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardStudentResponse {

    private int submittedThesisPlans;
    private String currentPlanStatus;
    private int pendingProcedures;
    private int uploadedDocuments;
    private int openCallsForApplication;

    private String groupName;
    private String groupCode;

    private List<AlertItemResponse> alerts;
}
