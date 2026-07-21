package com.sgi.fiis.tramites.application.dto;

import com.sgi.fiis.tramites.domain.model.ProcedureType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureRequestDto {

    @NotNull(message = "El tipo de trámite es obligatorio")
    private ProcedureType procedureType;

    // Poblado por el controller desde el JWT — el cliente no lo envía
    private Long applicantId;

    private Long groupId;

    // Arco excluyente — exactamente uno debe ser no nulo
    private Long projectReferenceId;
    private Long thesisReferenceId;
    private Long reportReferenceId;
}
