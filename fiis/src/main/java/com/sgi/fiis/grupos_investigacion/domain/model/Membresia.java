package com.sgi.fiis.grupos_investigacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Membresia {
    private Integer id;
    private Integer idGrupo;
    private Integer idUsuario;
    private String usuarioNombres;
    private String usuarioApellidos;
    private String usuarioCorreo;
    private boolean esActivo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public void retirar() {
        this.esActivo = false;
        this.fechaFin = LocalDateTime.now(ZoneId.of("UTC"));
    }
}
