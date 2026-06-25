package com.sgi.fiis.convocatorias.application.usecases;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.in.CreateCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;

import org.springframework.stereotype.Service;

@Service
public class CreateCallInteractor implements CreateCallUseCase {

    private final SaveCallPort saveCallPort;

    public CreateCallInteractor(SaveCallPort saveCallPort) {
        this.saveCallPort = saveCallPort;
    }

    @Override
    @Auditable(action = "CREATE_RESEARCH_CALL")
    public CallResponse execute(CreateCallRequest request) {
        // RN-11: Validate that all research lines are active
        if (!saveCallPort.areLinesActive(request.getResearchLineIds())) {
            throw new BusinessRuleValidationException("convocatorias.error.lines-not-active");
        }

        // Create domain model which executes business rule checks (e.g. endDate is not before startDate)
        ResearchCall call = new ResearchCall(
                null,
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                CallStatus.OPEN,
                request.getDocumentId(),
                request.getResearchLineIds()
        );

        ResearchCall savedCall = saveCallPort.save(call);

        String statusName = "ABIERTA";
        if (savedCall.getStatus() == CallStatus.CLOSED) {
            statusName = "CERRADA";
        } else if (savedCall.getStatus() == CallStatus.FINISHED) {
            statusName = "FINALIZADA";
        }

        return new CallResponse(
                savedCall.getId(),
                savedCall.getTitle(),
                savedCall.getDescription(),
                savedCall.getStartDate(),
                savedCall.getEndDate(),
                statusName,
                savedCall.getDocumentId(),
                savedCall.getResearchLineIds()
        );
    }

}
