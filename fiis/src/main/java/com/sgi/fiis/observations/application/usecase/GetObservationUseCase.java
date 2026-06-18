package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.observations.application.dto.ObservationResponseDTO;
import com.sgi.fiis.observations.domain.exception.ObservationNotFoundException;
import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.port.ObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: Retrieve details of an observation.
 */
@Service
@RequiredArgsConstructor
public class GetObservationUseCase {

    private final ObservationRepository observationRepository;

    @Transactional(readOnly = true)
    public ObservationResponseDTO execute(Integer observationId) {
        Observation o = observationRepository.findById(observationId)
                .orElseThrow(() -> new ObservationNotFoundException(observationId));

        return ObservationResponseDTO.builder()
                .id(o.getId())
                .procedureId(o.getProcedureId())
                .reviewerId(o.getReviewerId())
                .type(o.getType().name())
                .description(o.getDescription())
                .status(o.getStatus().name())
                .reviewerRole(o.getReviewerRole())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
