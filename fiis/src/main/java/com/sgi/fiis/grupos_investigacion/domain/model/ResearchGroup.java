package com.sgi.fiis.grupos_investigacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchGroup {
    private Integer id;
    private String groupCode;
    private String groupName;
    private Integer currentCoordinatorId;
    private String coordinatorFirstNames;
    private String coordinatorLastNames;
    private boolean active;
    private LocalDateTime createdAt;
}
