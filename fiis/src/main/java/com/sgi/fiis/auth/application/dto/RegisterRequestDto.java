package com.sgi.fiis.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for self-registration request (Sign Up).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "{validation.dni.required}")
    @Size(min = 8, max = 8, message = "{validation.dni.size}")
    private String dni;

    @NotBlank(message = "{validation.nombres.required}")
    @Size(max = 100, message = "{validation.nombres.size}")
    private String firstNames;

    @NotBlank(message = "{validation.apellidos.required}")
    @Size(max = 100, message = "{validation.apellidos.size}")
    private String lastNames;

    @NotBlank(message = "{validation.correo.required}")
    @Size(max = 150, message = "{validation.correo.size}")
    private String institutionalEmail;

    @Size(max = 20, message = "{validation.telefono.size}")
    private String phone;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 6, max = 100, message = "{validation.password.size}")
    private String password;

    @NotBlank(message = "{validation.confirmarPassword.required}")
    private String confirmPassword;

    @NotBlank(message = "{validation.rolCodigo.required}")
    private String roleCode;
}
