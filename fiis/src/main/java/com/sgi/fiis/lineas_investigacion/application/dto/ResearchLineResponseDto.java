package com.sgi.fiis.lineas_investigacion.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchLineResponseDto {
    private Integer id;
    private String lineName;
    private boolean active;
    private String createdAt;
    private String updatedAt;
}
