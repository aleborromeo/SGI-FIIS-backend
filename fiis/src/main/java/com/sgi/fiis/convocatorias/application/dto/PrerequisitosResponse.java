package com.sgi.fiis.convocatorias.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrerequisitosResponse {
    private boolean hasActiveGroup;
    private boolean hasVigentCalls;
    private boolean docente;
    private boolean valid;
}
