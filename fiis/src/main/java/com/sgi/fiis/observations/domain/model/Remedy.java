package com.sgi.fiis.observations.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Domain entity representing a remedy submitted to resolve an observation.
 */
@Getter
@Builder
public class Remedy {

    private Integer id;
    private Integer observationId;
    private Integer applicantId;
    private String description;
    private Integer attachedDocumentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Remedy create(Integer observationId, Integer applicantId,
                                String description, Integer attachedDocumentId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return Remedy.builder()
                .observationId(observationId)
                .applicantId(applicantId)
                .description(description)
                .attachedDocumentId(attachedDocumentId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public boolean hasAttachedDocument() {
        return this.attachedDocumentId != null;
    }
}
