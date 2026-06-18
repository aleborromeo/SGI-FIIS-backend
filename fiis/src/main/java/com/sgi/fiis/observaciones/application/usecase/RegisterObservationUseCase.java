package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.ObservationRequestDto;
import com.sgi.fiis.observaciones.application.dto.ObservationResponseDto;
import com.sgi.fiis.observaciones.domain.model.Observation;
import com.sgi.fiis.observaciones.domain.model.ObservationType;
import com.sgi.fiis.observaciones.domain.port.ObservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: Register a new observation on a procedure.
 */
@Service
@RequiredArgsConstructor
public class RegisterObservationUseCase {

    private final ObservationRepositoryPort observationRepository;

    @Transactional
    public ObservationResponseDto execute(ObservationRequestDto dto) {
        ObservationType type = ObservationType.valueOf(dto.getObservationType());

        Observation observation = Observation.create(
                dto.getProcedureId(),
                dto.getReviewerId(),
                type,
                dto.getDescription(),
                dto.getReviewerRole()
        );

        Observation saved = observationRepository.save(observation);
        return toResponseDto(saved);
    }

    private ObservationResponseDto toResponseDto(Observation observation) {
        return ObservationResponseDto.builder()
                .id(observation.getId())
                .procedureId(observation.getProcedureId())
                .reviewerId(observation.getReviewerId())
                .observationType(observation.getObservationType().name())
                .description(observation.getDescription())
                .status(observation.getStatus().name())
                .reviewerRole(observation.getReviewerRole())
                .registeredAt(observation.getRegisteredAt())
                .updatedAt(observation.getUpdatedAt())
                .build();
    }
}
