package com.sgi.fiis.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for resending verification code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendCodeRequestDto {

    @NotBlank(message = "{validation.correo.required}")
    @Size(max = 150, message = "{validation.correo.size}")
    private String email;
}
