package com.sgi.fiis.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String tipo;
    private String correo;
    private String nombres;
    private String apellidos;
    private String rolCodigo;
    private boolean mustChangePassword;
    private boolean requiresVerification;
}
