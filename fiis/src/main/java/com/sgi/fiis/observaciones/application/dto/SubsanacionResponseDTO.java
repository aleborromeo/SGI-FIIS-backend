package com.sgi.fiis.observaciones.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubsanacionResponseDTO {
    private Integer id;
    private Integer idObservacion;
    private Integer idSolicitante;
    private String descripcion;
    private Integer idDocumentoAdjunto;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
}
