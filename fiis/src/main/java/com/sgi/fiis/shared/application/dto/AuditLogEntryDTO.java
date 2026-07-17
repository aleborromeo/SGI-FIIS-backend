package com.sgi.fiis.shared.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLogEntryDTO {
    private Long id;
    private String tablaAfectada;
    private Long idRegistro;
    private String accion;
    private Long idUsuario;
    private String nombreUsuario;
    private String datosAnteriores;
    private String datosNuevos;
    private String ipOrigen;
    private LocalDateTime fechaAccion;
}
