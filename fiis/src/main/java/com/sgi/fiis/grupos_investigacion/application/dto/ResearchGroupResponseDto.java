package com.sgi.fiis.grupos_investigacion.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchGroupResponseDto {
    private Integer id;
    private String groupCode;
    private String groupName;
    private Integer currentCoordinatorId;
    private String coordinatorFirstNames;
    private String coordinatorLastNames;
    private boolean active;
}
