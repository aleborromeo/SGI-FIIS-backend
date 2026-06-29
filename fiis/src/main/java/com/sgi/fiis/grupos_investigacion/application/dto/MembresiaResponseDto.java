package com.sgi.fiis.grupos_investigacion.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembresiaResponseDto {
    private Integer id;
    private Integer idGrupo;
    private Integer idUsuario;
    private String usuarioNombres;
    private String usuarioApellidos;
    private String usuarioCorreo;
    private boolean esActivo;
    private String fechaInicio;
    private String fechaFin;
}
