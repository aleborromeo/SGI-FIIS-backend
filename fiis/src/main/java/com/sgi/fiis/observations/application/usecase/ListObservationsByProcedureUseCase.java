package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.observations.application.dto.ObservationResponseDTO;
import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.port.ObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Use case: List observations for a procedure.
 */
@Service
@RequiredArgsConstructor
public class ListObservationsByProcedureUseCase {

    private final ObservationRepository observationRepository;

    @Transactional(readOnly = true)
    public List<ObservationResponseDTO> execute(Integer procedureId) {
        return observationRepository.findByProcedureId(procedureId).stream()
                .map(this::toDTO).toList();
    }

    private ObservationResponseDTO toDTO(Observation o) {
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
