package com.sgi.fiis.observaciones.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Domain entity representing a correction submitted to resolve an observation.
 */
@Getter
@Builder
public class Correction {

    private Integer id;
    private Integer observationId;
    private Integer requesterId;
    private String description;
    private Integer attachedDocumentId;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;

    public static Correction create(Integer observationId, Integer requesterId,
                                    String description, Integer attachedDocumentId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return Correction.builder()
                .observationId(observationId)
                .requesterId(requesterId)
                .description(description)
                .attachedDocumentId(attachedDocumentId)
                .registeredAt(now)
                .updatedAt(now)
                .build();
    }

    public boolean hasAttachedDocument() {
        return this.attachedDocumentId != null;
    }
}
