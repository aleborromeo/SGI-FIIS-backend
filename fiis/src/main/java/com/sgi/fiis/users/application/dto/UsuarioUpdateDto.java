package com.sgi.fiis.users.application.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para editar datos de un usuario (RF-08).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDto {

    @Size(max = 100, message = "Los nombres no deben exceder 100 caracteres")
    private String nombres;

    @Size(max = 100, message = "Los apellidos no deben exceder 100 caracteres")
    private String apellidos;

    private String correoInstitucional;

    @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres")
    private String telefono;

    private String rolCodigo;
}
