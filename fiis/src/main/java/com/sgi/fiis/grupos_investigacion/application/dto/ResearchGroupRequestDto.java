package com.sgi.fiis.grupos_investigacion.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchGroupRequestDto {

    @NotBlank(message = "Group code is required")
    @Size(max = 20, message = "Group code must not exceed 20 characters")
    private String groupCode;

    @NotBlank(message = "Group name is required")
    @Size(max = 150, message = "Group name must not exceed 150 characters")
    private String groupName;
}
