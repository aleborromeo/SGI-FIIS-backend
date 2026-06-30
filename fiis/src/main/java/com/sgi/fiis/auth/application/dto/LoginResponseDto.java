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
    private String type;
    private String email;
    private String firstNames;
    private String lastNames;
    private String roleCode;
    private boolean mustChangePassword;
    private boolean requiresVerification;
}
