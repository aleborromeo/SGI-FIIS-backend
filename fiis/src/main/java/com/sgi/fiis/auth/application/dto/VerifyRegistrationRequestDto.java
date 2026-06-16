package com.sgi.fiis.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de verificación de registro.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyRegistrationRequestDto {

    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 6, max = 6, message = "El código debe tener exactamente 6 dígitos")
    private String codigo;
}
