package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.ObservationResponseDto;
import com.sgi.fiis.observaciones.domain.exception.ObservationNotFoundException;
import com.sgi.fiis.observaciones.domain.model.Observation;
import com.sgi.fiis.observaciones.domain.port.ObservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: Get detail of a single observation (RF-61).
 */
@Service
@RequiredArgsConstructor
public class GetObservationUseCase {

    private final ObservationRepositoryPort observationRepository;

    @Transactional(readOnly = true)
    public ObservationResponseDto execute(Integer observationId) {
        Observation o = observationRepository.findById(observationId)
                .orElseThrow(() -> new ObservationNotFoundException(observationId));

        return ObservationResponseDto.builder()
                .id(o.getId())
                .procedureId(o.getProcedureId())
                .reviewerId(o.getReviewerId())
                .observationType(o.getObservationType().name())
                .description(o.getDescription())
                .status(o.getStatus().name())
                .reviewerRole(o.getReviewerRole())
                .registeredAt(o.getRegisteredAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
