package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.observations.application.dto.ObservationRequestDTO;
import com.sgi.fiis.observations.application.dto.ObservationResponseDTO;
import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.model.ObservationType;
import com.sgi.fiis.observations.domain.port.ObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case to register a new observation on a procedure.
 */
@Service
@RequiredArgsConstructor
public class RegisterObservationUseCase {

    private final ObservationRepository observationRepository;

    @Transactional
    public ObservationResponseDTO execute(ObservationRequestDTO dto) {
        ObservationType type = ObservationType.valueOf(dto.getType());

        Observation observation = Observation.create(
                dto.getProcedureId(),
                dto.getReviewerId(),
                type,
                dto.getDescription(),
                dto.getReviewerRole()
        );

        Observation saved = observationRepository.save(observation);
        return toResponseDTO(saved);
    }

    private ObservationResponseDTO toResponseDTO(Observation observation) {
        return ObservationResponseDTO.builder()
                .id(observation.getId())
                .procedureId(observation.getProcedureId())
                .reviewerId(observation.getReviewerId())
                .type(observation.getType().name())
                .description(observation.getDescription())
                .status(observation.getStatus().name())
                .reviewerRole(observation.getReviewerRole())
                .createdAt(observation.getCreatedAt())
                .updatedAt(observation.getUpdatedAt())
                .build();
    }
}
