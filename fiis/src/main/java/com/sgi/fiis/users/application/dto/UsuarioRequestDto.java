package com.sgi.fiis.users.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear un usuario (RF-07).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDto {

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 caracteres")
    private String dni;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no deben exceder 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no deben exceder 100 caracteres")
    private String apellidos;

    private String correoInstitucional; // Opcional, se genera automáticamente si está vacío (RF-10)

    @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres")
    private String telefono;

    @NotBlank(message = "El código de rol es obligatorio")
    private String rolCodigo;
}
