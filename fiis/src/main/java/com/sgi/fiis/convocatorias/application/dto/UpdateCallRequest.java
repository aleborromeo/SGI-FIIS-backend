package com.sgi.fiis.convocatorias.application.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCallRequest {

    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer documentId;
    private List<Integer> researchLineIds;
}
