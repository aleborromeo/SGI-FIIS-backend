package com.sgi.fiis.convocatorias.application.usecases;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.UpdateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.in.UpdateCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCallInteractor implements UpdateCallUseCase {

    private static final String STATUS_ABIERTA = "ABIERTA";
    private static final String STATUS_CERRADA = "CERRADA";
    private static final String STATUS_FINALIZADA = "FINALIZADA";

    private final SaveCallPort saveCallPort;

    public UpdateCallInteractor(SaveCallPort saveCallPort) {
        this.saveCallPort = saveCallPort;
    }

    @Override
    @Transactional
    @Auditable(action = "UPDATE_RESEARCH_CALL")
    public CallResponse execute(Integer id, UpdateCallRequest request) {
        ResearchCall call = saveCallPort.findById(id)
                .orElseThrow(() -> new BusinessRuleValidationException("convocatorias.error.not-found", id));

        if (call.getStatus() != CallStatus.OPEN) {
            throw new BusinessRuleValidationException("convocatorias.error.cannot-edit-closed", call.getStatus());
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            call = ResearchCall.builder()
                    .id(call.getId())
                    .title(request.getTitle())
                    .description(request.getDescription() != null ? request.getDescription() : call.getDescription())
                    .startDate(request.getStartDate() != null ? request.getStartDate() : call.getStartDate())
                    .endDate(request.getEndDate() != null ? request.getEndDate() : call.getEndDate())
                    .status(call.getStatus())
                    .documentId(request.getDocumentId() != null ? request.getDocumentId() : call.getDocumentId())
                    .creatorId(call.getCreatorId())
                    .researchLineIds(request.getResearchLineIds() != null ? request.getResearchLineIds() : call.getResearchLineIds())
                    .build();
        } else {
            call = ResearchCall.builder()
                    .id(call.getId())
                    .title(call.getTitle())
                    .description(request.getDescription() != null ? request.getDescription() : call.getDescription())
                    .startDate(request.getStartDate() != null ? request.getStartDate() : call.getStartDate())
                    .endDate(request.getEndDate() != null ? request.getEndDate() : call.getEndDate())
                    .status(call.getStatus())
                    .documentId(request.getDocumentId() != null ? request.getDocumentId() : call.getDocumentId())
                    .creatorId(call.getCreatorId())
                    .researchLineIds(request.getResearchLineIds() != null ? request.getResearchLineIds() : call.getResearchLineIds())
                    .build();
        }

        if (request.getResearchLineIds() != null && !request.getResearchLineIds().isEmpty()) {
            if (!saveCallPort.areLinesActive(request.getResearchLineIds())) {
                throw new BusinessRuleValidationException("convocatorias.error.lines-not-active");
            }
        }

        ResearchCall saved = saveCallPort.save(call);
        return mapToResponse(saved);
    }

    private CallResponse mapToResponse(ResearchCall call) {
        String statusName = STATUS_ABIERTA;
        if (call.getStatus() == CallStatus.CLOSED) {
            statusName = STATUS_CERRADA;
        } else if (call.getStatus() == CallStatus.FINISHED) {
            statusName = STATUS_FINALIZADA;
        }
        return new CallResponse(
                call.getId(),
                call.getTitle(),
                call.getDescription(),
                call.getStartDate(),
                call.getEndDate(),
                statusName,
                call.getDocumentId(),
                call.getResearchLineIds()
        );
    }
}
