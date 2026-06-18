package com.sgi.fiis.observaciones.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObservacionResponseDTO {
    private Integer id;
    private Integer idTramite;
    private Integer idRevisor;
    private String tipoObservacion;
    private String descripcion;
    private String estado;
    private String rolRevisor;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
}
