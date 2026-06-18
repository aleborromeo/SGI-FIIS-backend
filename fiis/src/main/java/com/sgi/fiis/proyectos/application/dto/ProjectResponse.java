package com.sgi.fiis.proyectos.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Integer id;
    private String code;
    private String title;
    private String summary;
    private String generalObjective;
    private Integer researchLineId;
    private String researchLineName;
    private BigDecimal budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private String executionPlace;
    private Long responsibleId;
    private Integer researchGroupId;
    private String researchGroupCode;
    private Integer callId;
    private Integer documentId;
    private String status;
    private List<MemberResponse> members;

}
