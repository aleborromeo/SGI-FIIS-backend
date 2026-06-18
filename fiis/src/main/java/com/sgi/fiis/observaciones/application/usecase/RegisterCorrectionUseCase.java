package com.sgi.fiis.observaciones.application.usecase;

import com.sgi.fiis.observaciones.application.dto.CorrectionRequestDto;
import com.sgi.fiis.observaciones.application.dto.CorrectionResponseDto;
import com.sgi.fiis.observaciones.domain.exception.ObservationNotFoundException;
import com.sgi.fiis.observaciones.domain.exception.InvalidCorrectionException;
import com.sgi.fiis.observaciones.domain.model.Observation;
import com.sgi.fiis.observaciones.domain.model.Correction;
import com.sgi.fiis.observaciones.domain.port.ObservationRepositoryPort;
import com.sgi.fiis.observaciones.domain.port.CorrectionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: Register a correction to resolve a pending observation (RF-62, RF-63).
 */
@Service
@RequiredArgsConstructor
public class RegisterCorrectionUseCase {

    private final ObservationRepositoryPort observationRepository;
    private final CorrectionRepositoryPort correctionRepository;

    @Transactional
    public CorrectionResponseDto execute(Integer observationId, CorrectionRequestDto dto) {
        Observation observation = observationRepository.findById(observationId)
                .orElseThrow(() -> new ObservationNotFoundException(observationId));

        if (!observation.isResolvable()) {
            throw new InvalidCorrectionException(observationId);
        }

        Correction correction = Correction.create(
                observationId, dto.getRequesterId(),
                dto.getDescription(), dto.getAttachedDocumentId());

        observation.markResolved();
        observationRepository.save(observation);
        Correction saved = correctionRepository.save(correction);

        return CorrectionResponseDto.builder()
                .id(saved.getId())
                .observationId(saved.getObservationId())
                .requesterId(saved.getRequesterId())
                .description(saved.getDescription())
                .attachedDocumentId(saved.getAttachedDocumentId())
                .registeredAt(saved.getRegisteredAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
