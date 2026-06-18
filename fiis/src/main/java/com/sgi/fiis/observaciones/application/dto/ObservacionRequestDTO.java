package com.sgi.fiis.observaciones.application.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObservacionRequestDTO {
    private Integer idTramite;
    private Integer idRevisor;
    private String tipoObservacion;
    private String descripcion;
    private String rolRevisor;
}
