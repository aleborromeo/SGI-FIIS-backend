package com.sgi.fiis.convocatorias.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallResponse {
    private Integer id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
