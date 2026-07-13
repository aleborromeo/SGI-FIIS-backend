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

        call = applyUpdates(call, request);
        validateActiveLines(request);

        ResearchCall saved = saveCallPort.save(call);
        return mapToResponse(saved);
    }

    private ResearchCall applyUpdates(ResearchCall call, UpdateCallRequest request) {
        return ResearchCall.builder()
                .id(call.getId())
                .title(resolveString(request.getTitle(), call.getTitle()))
                .description(resolveString(request.getDescription(), call.getDescription()))
                .startDate(resolveIfNotNull(request.getStartDate(), call.getStartDate()))
                .endDate(resolveIfNotNull(request.getEndDate(), call.getEndDate()))
                .status(call.getStatus())
                .documentId(resolveIfNotNull(request.getDocumentId(), call.getDocumentId()))
                .creatorId(call.getCreatorId())
                .researchLineIds(request.getResearchLineIds() != null
                        ? request.getResearchLineIds() : call.getResearchLineIds())
                .build();
    }

    private void validateActiveLines(UpdateCallRequest request) {
        if (request.getResearchLineIds() != null && !request.getResearchLineIds().isEmpty()
                && !saveCallPort.areLinesActive(request.getResearchLineIds())) {
            throw new BusinessRuleValidationException("convocatorias.error.lines-not-active");
        }
    }

    private String resolveString(String candidate, String fallback) {
        return (candidate != null && !candidate.isBlank()) ? candidate : fallback;
    }

    private <T> T resolveIfNotNull(T candidate, T fallback) {
        return candidate != null ? candidate : fallback;
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
