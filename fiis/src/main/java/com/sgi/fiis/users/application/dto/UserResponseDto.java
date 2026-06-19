package com.sgi.fiis.users.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Output DTO for User responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String dni;
    private String firstNames;
    private String lastNames;
    private String institutionalEmail;
    private String phone;
    private boolean active;
    private boolean mustChangePassword;
    private String roleCode;
    private String roleDescription;
    private String createdAt;
    private String updatedAt;
}
