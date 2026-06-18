package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlertItem {

    private String type;
    private String title;
    private String description;
}
