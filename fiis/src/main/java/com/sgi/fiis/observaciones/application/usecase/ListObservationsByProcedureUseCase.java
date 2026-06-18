package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.ObservationResponseDto;
import com.sgi.fiis.observaciones.domain.model.Observation;
import com.sgi.fiis.observaciones.domain.port.ObservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Use case: List all observations for a procedure (RF-64 traceability).
 */
@Service
@RequiredArgsConstructor
public class ListObservationsByProcedureUseCase {

    private final ObservationRepositoryPort observationRepository;

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> execute(Integer procedureId) {
        return observationRepository.findByProcedureId(procedureId).stream()
                .map(this::toDto).toList();
    }

    private ObservationResponseDto toDto(Observation o) {
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
