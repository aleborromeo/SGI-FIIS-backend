package com.sgi.fiis.observaciones.infrastructure.persistence.mapper;

import com.sgi.fiis.observaciones.domain.model.Correction;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.CorrectionEntity;
import org.springframework.stereotype.Component;

/**
 * Bidirectional mapper between {@link Correction} (domain) and {@link CorrectionEntity} (JPA).
 */
@Component
public class CorrectionMapper {

    public Correction toDomain(CorrectionEntity entity) {
        return Correction.builder()
                .id(entity.getId())
                .observationId(entity.getObservationId())
                .requesterId(entity.getRequesterId())
                .description(entity.getDescription())
                .attachedDocumentId(entity.getAttachedDocumentId())
                .registeredAt(entity.getRegisteredAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CorrectionEntity toEntity(Correction domain) {
        return CorrectionEntity.builder()
                .id(domain.getId())
                .observationId(domain.getObservationId())
                .requesterId(domain.getRequesterId())
                .description(domain.getDescription())
                .attachedDocumentId(domain.getAttachedDocumentId())
                .registeredAt(domain.getRegisteredAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
