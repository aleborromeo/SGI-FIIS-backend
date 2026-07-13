package com.sgi.fiis.grupos_investigacion.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponseDto {
    private Integer id;
    private Integer groupId;
    private Integer userId;
    private String userFirstNames;
    private String userLastNames;
    private String userEmail;
    private String userRoleCode;
    private boolean active;
    private String startDate;
    private String endDate;
}
