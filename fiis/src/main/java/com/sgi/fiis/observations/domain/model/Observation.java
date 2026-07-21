package com.sgi.fiis.observations.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Domain entity representing an observation recorded on a procedure.
 */
@Getter
@Builder
public class Observation {

    private final Integer id;
    private final Integer procedureId;
    private final Integer reviewerId;
    private final ObservationType type;
    private final String description;
    private ObservationStatus status;
    private final String reviewerRole;
    private final LocalDateTime createdAt;

    @SuppressWarnings("java:S1450")
    private LocalDateTime updatedAt;

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static Observation create(Integer procedureId, Integer reviewerId,
                                     ObservationType type,
                                     String description, String reviewerRole) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return Observation.builder()
                .procedureId(procedureId)
                .reviewerId(reviewerId)
                .type(type)
                .description(description)
                .status(ObservationStatus.PENDIENTE)
                .reviewerRole(reviewerRole)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public boolean markAsRemedied() {
        if (this.status != ObservationStatus.PENDIENTE) {
            return false;
        }
        this.status = ObservationStatus.SUBSANADA;
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

    public boolean isRemediable() {
        return this.status == ObservationStatus.PENDIENTE;
    }
}
