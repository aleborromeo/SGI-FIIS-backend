package com.sgi.fiis.grupos_investigacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoInvestigacion {
    private Integer id;
    private String codigoGrupo;
    private String nombreGrupo;
    private Integer idCoordinadorActual;
    private String coordinadorNombres;
    private String coordinadorApellidos;
    private boolean esActivo;
}
