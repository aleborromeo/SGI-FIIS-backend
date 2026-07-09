package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.observations.application.dto.RemedyRequestDTO;
import com.sgi.fiis.observations.application.dto.RemedyResponseDTO;
import com.sgi.fiis.observations.domain.exception.ObservationNotFoundException;
import com.sgi.fiis.observations.domain.exception.InvalidRemedyException;
import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.model.Remedy;
import com.sgi.fiis.observations.domain.port.ObservationRepository;
import com.sgi.fiis.observations.domain.port.RemedyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: Register a remedy for an observation.
 */
@Service
@RequiredArgsConstructor
public class RegisterRemedyUseCase {

    private final ObservationRepository observationRepository;
    private final RemedyRepository remedyRepository;

    @Transactional
    public RemedyResponseDTO execute(Integer observationId, RemedyRequestDTO dto) {
        Observation observation = observationRepository.findById(observationId)
                .orElseThrow(() -> new ObservationNotFoundException(observationId));

        if (!observation.isRemediable()) {
            throw new InvalidRemedyException(observationId);
        }

        Remedy remedy = Remedy.create(
                observationId, dto.getApplicantId(),
                dto.getDescription(), dto.getAttachedDocumentId());

        observation.markAsRemedied();
        observationRepository.save(observation);
        Remedy saved = remedyRepository.save(remedy);

        return RemedyResponseDTO.builder()
                .id(saved.getId())
                .observationId(saved.getObservationId())
                .applicantId(saved.getApplicantId())
                .description(saved.getDescription())
                .attachedDocumentId(saved.getAttachedDocumentId())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
