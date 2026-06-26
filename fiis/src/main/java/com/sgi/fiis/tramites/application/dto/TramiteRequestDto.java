package com.sgi.fiis.tramites.application.dto;

import com.sgi.fiis.tramites.domain.model.TipoTramite;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TramiteRequestDto {

    @NotNull(message = "El tipo de trámite es obligatorio")
    private TipoTramite tipoTramite;

    // Poblado por el controller desde el JWT — el cliente no lo envía
    private Long idSolicitante;

    private Long idGrupo;

    // Arco excluyente — exactamente uno debe ser no nulo
    private Long idReferenciaProyecto;
    private Long idReferenciaTesis;
    private Long idReferenciaInforme;
}
