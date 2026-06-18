package com.sgi.fiis.users.application.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating a user's data (RF-08).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @Size(max = 100, message = "Los nombres no deben exceder 100 caracteres")
    private String firstNames;

    @Size(max = 100, message = "Los apellidos no deben exceder 100 caracteres")
    private String lastNames;

    private String institutionalEmail;

    @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres")
    private String phone;

    private String roleCode;
}
