package com.sgi.fiis.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de reenvío de código de verificación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendCodeRequestDto {

    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 150, message = "El correo no debe exceder 150 caracteres")
    private String correo;
}
