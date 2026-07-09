package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.observations.application.dto.RemedyResponseDTO;
import com.sgi.fiis.observations.domain.model.Remedy;
import com.sgi.fiis.observations.domain.port.RemedyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Use case: List remedies for an observation.
 */
@Service
@RequiredArgsConstructor
public class ListRemediesByObservationUseCase {

    private final RemedyRepository remedyRepository;

    @Transactional(readOnly = true)
    public List<RemedyResponseDTO> execute(Integer observationId) {
        return remedyRepository.findByObservationId(observationId).stream()
                .map(this::toDTO).toList();
    }

    private RemedyResponseDTO toDTO(Remedy s) {
        return RemedyResponseDTO.builder()
                .id(s.getId())
                .observationId(s.getObservationId())
                .applicantId(s.getApplicantId())
                .description(s.getDescription())
                .attachedDocumentId(s.getAttachedDocumentId())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
