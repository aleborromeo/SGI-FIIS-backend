package com.sgi.fiis.observations.infrastructure.persistence.mapper;

import com.sgi.fiis.observations.domain.model.Remedy;
import com.sgi.fiis.observations.infrastructure.persistence.entity.RemedyJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Bidirectional mapper between {@link Remedy} (domain) and {@link RemedyJpaEntity} (JPA).
 */
@Component
public class RemedyMapper {

    public Remedy toDomain(RemedyJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Remedy.builder()
                .id(entity.getId())
                .observationId(entity.getObservationId())
                .applicantId(entity.getApplicantId())
                .description(entity.getDescription())
                .attachedDocumentId(entity.getAttachedDocumentId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public RemedyJpaEntity toJpa(Remedy domain) {
        if (domain == null) {
            return null;
        }
        return RemedyJpaEntity.builder()
                .id(domain.getId())
                .observationId(domain.getObservationId())
                .applicantId(domain.getApplicantId())
                .description(domain.getDescription())
                .attachedDocumentId(domain.getAttachedDocumentId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
