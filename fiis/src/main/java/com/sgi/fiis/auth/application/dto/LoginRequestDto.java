package com.sgi.fiis.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "{validation.correo.required}")
    private String email;

    @NotBlank(message = "{validation.password.required}")
    private String password;
}
