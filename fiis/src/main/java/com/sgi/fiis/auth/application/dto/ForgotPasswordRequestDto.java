package com.sgi.fiis.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDto {
    @NotBlank(message = "{validation.correo.required}")
    @Email(message = "{validation.correo.invalid}")
    private String email;
}
