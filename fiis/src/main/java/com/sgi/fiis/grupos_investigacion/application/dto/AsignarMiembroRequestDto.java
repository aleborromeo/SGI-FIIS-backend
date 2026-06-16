package com.sgi.fiis.grupos_investigacion.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarMiembroRequestDto {

    @NotNull(message = "El id del usuario es obligatorio")
    private Integer idUsuario;
}
