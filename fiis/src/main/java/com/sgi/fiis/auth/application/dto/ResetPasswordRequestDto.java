package com.sgi.fiis.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {
    @NotBlank(message = "{validation.correo.required}")
    @Email(message = "{validation.correo.invalid}")
    private String email;

    @NotBlank(message = "{validation.code.required}")
    @Size(min = 6, max = 6, message = "{validation.code.size}")
    private String code;

    @NotBlank(message = "{validation.newPassword.required}")
    @Size(min = 6, message = "{validation.newPassword.size}")
    private String newPassword;

    @NotBlank(message = "{validation.confirmPassword.required}")
    private String confirmPassword;
}
