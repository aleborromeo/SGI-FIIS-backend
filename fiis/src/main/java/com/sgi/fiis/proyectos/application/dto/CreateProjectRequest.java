package com.sgi.fiis.proyectos.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

    private String title;

    private String summary;

    private String generalObjective;

    private Integer researchLineId;

    private BigDecimal budget;

    private LocalDate startDate;

    private LocalDate endDate;

    private String executionPlace;

    // Set automatically from the authenticated user in the controller (RF-39)
    private Integer responsibleId;

    private Integer researchGroupId;

    private Integer callId;

    private Integer documentId;

    private List<MemberRequest> members;

    private boolean draft;
}
