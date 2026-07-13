package com.sgi.fiis.grupos_investigacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Membership {
    private Integer id;
    private Integer groupId;
    private Integer userId;
    private String userFirstNames;
    private String userLastNames;
    private String userEmail;
    private String userRoleCode;
    private boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public void remove() {
        this.active = false;
        this.endDate = LocalDateTime.now(java.time.ZoneId.systemDefault());
    }
}
