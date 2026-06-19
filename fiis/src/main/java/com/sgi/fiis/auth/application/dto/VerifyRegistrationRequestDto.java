package com.sgi.fiis.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for verification registration request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyRegistrationRequestDto {

    @NotBlank(message = "{validation.correo.required}")
    private String email;

    @NotBlank(message = "{validation.codigo.required}")
    @Size(min = 6, max = 6, message = "{validation.codigo.size}")
    private String code;
}
