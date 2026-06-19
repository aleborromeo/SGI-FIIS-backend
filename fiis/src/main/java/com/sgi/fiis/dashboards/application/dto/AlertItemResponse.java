package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlertItemResponse {

    private String type;
    private String title;
    private String description;
}
