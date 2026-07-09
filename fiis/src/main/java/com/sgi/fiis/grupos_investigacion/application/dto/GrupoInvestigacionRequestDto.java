package com.sgi.fiis.grupos_investigacion.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoInvestigacionRequestDto {

    @NotBlank(message = "{grupos.validation.codigo.required}")
    @Size(max = 20, message = "{grupos.validation.codigo.max-size}")
    private String codigoGrupo;

    @NotBlank(message = "{grupos.validation.nombre.required}")
    @Size(max = 150, message = "{grupos.validation.nombre.max-size}")
    private String nombreGrupo;
}
