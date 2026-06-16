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

    @NotBlank(message = "El código del grupo es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    private String codigoGrupo;

    @NotBlank(message = "El nombre del grupo es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombreGrupo;
}
