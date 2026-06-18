package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.CorrectionResponseDto;
import com.sgi.fiis.observaciones.domain.model.Correction;
import com.sgi.fiis.observaciones.domain.port.CorrectionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Use case: List all corrections for a given observation (RF-64 traceability).
 */
@Service
@RequiredArgsConstructor
public class ListCorrectionsByObservationUseCase {

    private final CorrectionRepositoryPort correctionRepository;

    @Transactional(readOnly = true)
    public List<CorrectionResponseDto> execute(Integer observationId) {
        return correctionRepository.findByObservationId(observationId).stream()
                .map(this::toDto).toList();
    }

    private CorrectionResponseDto toDto(Correction c) {
        return CorrectionResponseDto.builder()
                .id(c.getId())
                .observationId(c.getObservationId())
                .requesterId(c.getRequesterId())
                .description(c.getDescription())
                .attachedDocumentId(c.getAttachedDocumentId())
                .registeredAt(c.getRegisteredAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
