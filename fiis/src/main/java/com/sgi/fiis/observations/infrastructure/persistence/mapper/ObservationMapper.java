package com.sgi.fiis.observations.infrastructure.persistence.mapper;

import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.model.ObservationStatus;
import com.sgi.fiis.observations.domain.model.ObservationType;
import com.sgi.fiis.observations.infrastructure.persistence.entity.ObservationJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Bidirectional mapper between {@link Observation} (domain) and {@link ObservationJpaEntity} (JPA).
 */
@Component
public class ObservationMapper {

    public Observation toDomain(ObservationJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Observation.builder()
                .id(entity.getId())
                .procedureId(entity.getProcedureId())
                .reviewerId(entity.getReviewerId())
                .type(mapTypeToDomain(entity.getType()))
                .description(entity.getDescription())
                .status(mapStatusToDomain(entity.getStatus()))
                .reviewerRole(entity.getReviewerRole())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ObservationJpaEntity toJpa(Observation domain) {
        if (domain == null) {
            return null;
        }
        return ObservationJpaEntity.builder()
                .id(domain.getId())
                .procedureId(domain.getProcedureId())
                .reviewerId(domain.getReviewerId())
                .type(mapTypeToJpa(domain.getType()))
                .description(domain.getDescription())
                .status(mapStatusToJpa(domain.getStatus()))
                .reviewerRole(domain.getReviewerRole())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private ObservationStatus mapStatusToDomain(String dbStatus) {
        if (dbStatus == null) return null;
        return ObservationStatus.valueOf(dbStatus);
    }

    private String mapStatusToJpa(ObservationStatus domainStatus) {
        if (domainStatus == null) return null;
        return domainStatus.name();
    }

    private ObservationType mapTypeToDomain(String dbType) {
        if (dbType == null) return null;
        return ObservationType.valueOf(dbType);
    }

    private String mapTypeToJpa(ObservationType domainType) {
        if (domainType == null) return null;
        return domainType.name();
    }
}
