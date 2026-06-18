package com.sgi.fiis.observaciones.infrastructure.persistence.mapper;

import com.sgi.fiis.observaciones.domain.model.Observation;
import com.sgi.fiis.observaciones.domain.model.ObservationStatus;
import com.sgi.fiis.observaciones.domain.model.ObservationType;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.ObservationEntity;
import org.springframework.stereotype.Component;

/**
 * Bidirectional mapper between {@link Observation} (domain) and {@link ObservationEntity} (JPA).
 */
@Component
public class ObservationMapper {

    public Observation toDomain(ObservationEntity entity) {
        return Observation.builder()
                .id(entity.getId())
                .procedureId(entity.getProcedureId())
                .reviewerId(entity.getReviewerId())
                .observationType(ObservationType.valueOf(entity.getObservationType()))
                .description(entity.getDescription())
                .status(ObservationStatus.valueOf(entity.getStatus()))
                .reviewerRole(entity.getReviewerRole())
                .registeredAt(entity.getRegisteredAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ObservationEntity toEntity(Observation domain) {
        return ObservationEntity.builder()
                .id(domain.getId())
                .procedureId(domain.getProcedureId())
                .reviewerId(domain.getReviewerId())
                .observationType(domain.getObservationType().name())
                .description(domain.getDescription())
                .status(domain.getStatus().name())
                .reviewerRole(domain.getReviewerRole())
                .registeredAt(domain.getRegisteredAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
