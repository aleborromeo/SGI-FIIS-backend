package com.sgi.fiis.observaciones.application.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubsanacionRequestDTO {
    private Integer idSolicitante;
    private String descripcion;
    private Integer idDocumentoAdjunto;
}
