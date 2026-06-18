package com.sgi.fiis.observaciones.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Domain entity representing an observation registered on a procedure.
 */
@Getter
@Builder
public class Observation {

    private Integer id;
    private Integer procedureId;
    private Integer reviewerId;
    private ObservationType observationType;
    private String description;
    private ObservationStatus status;
    private String reviewerRole;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;

    public static Observation create(Integer procedureId, Integer reviewerId,
                                     ObservationType observationType,
                                     String description, String reviewerRole) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return Observation.builder()
                .procedureId(procedureId)
                .reviewerId(reviewerId)
                .observationType(observationType)
                .description(description)
                .status(ObservationStatus.PENDING)
                .reviewerRole(reviewerRole)
                .registeredAt(now)
                .updatedAt(now)
                .build();
    }

    public boolean markResolved() {
        if (this.status != ObservationStatus.PENDING) {
            return false;
        }
        this.status = ObservationStatus.RESOLVED;
        this.updatedAt = LocalDateTime.now(ZoneId.systemDefault());
        return true;
    }

    public String determineReturnRole() {
        switch (this.reviewerRole) {
            case "DECANO":
                return "DIRECTOR_INVESTIGACION";
            case "DIRECTOR_INVESTIGACION":
                return "COORDINADOR_GRUPO";
            case "COORDINADOR_GRUPO":
            default:
                return null;
        }
    }

    public boolean isResolvable() {
        return this.status == ObservationStatus.PENDING;
    }
}
