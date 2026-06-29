package com.sgi.fiis.grupos_investigacion.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignMemberRequestDto {

    @NotNull(message = "User ID is required")
    private Integer userId;
}
