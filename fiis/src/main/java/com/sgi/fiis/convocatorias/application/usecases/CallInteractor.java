package com.sgi.fiis.convocatorias.application.usecases;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.ports.in.GetCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.UpdateCallStatusUseCase;
import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CallInteractor implements GetCallUseCase, UpdateCallStatusUseCase {

    private final SaveCallPort saveCallPort;

    public CallInteractor(SaveCallPort saveCallPort) {
        this.saveCallPort = saveCallPort;
    }

    // Support both getCalls and execute (used in tests) to ensure backward compatibility
    @Override
    @Transactional(readOnly = true)
    public List<CallResponse> getCalls(String status) {
        return execute(status);
    }

    @Transactional(readOnly = true)
    public List<CallResponse> execute(String status) {
        List<ResearchCall> calls;
        if (status != null && !status.trim().isEmpty()) {
            CallStatus callStatus;
            try {
                if ("ABIERTA".equalsIgnoreCase(status)) {
                    callStatus = CallStatus.OPEN;
                } else if ("CERRADA".equalsIgnoreCase(status)) {
                    callStatus = CallStatus.CLOSED;
                } else if ("FINALIZADA".equalsIgnoreCase(status)) {
                    callStatus = CallStatus.FINISHED;
                } else {
                    callStatus = CallStatus.valueOf(status.toUpperCase());
                }
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleValidationException("Invalid status filter: " + status);
            }
            calls = saveCallPort.findByStatus(callStatus);
        } else {
            calls = saveCallPort.findAll();
        }

        return calls.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CallResponse getCallById(Integer id) {
        ResearchCall call = saveCallPort.findById(id)
                .orElseThrow(() -> new BusinessRuleValidationException("Research call not found with ID: " + id));
        return mapToResponse(call);
    }

    @Override
    @Transactional
    @Auditable(action = "UPDATE_RESEARCH_CALL_STATUS")
    public CallResponse updateStatus(Integer id, String status) {
        ResearchCall call = saveCallPort.findById(id)
                .orElseThrow(() -> new BusinessRuleValidationException("Research call not found with ID: " + id));

        CallStatus callStatus;
        try {
            if ("ABIERTA".equalsIgnoreCase(status)) {
                callStatus = CallStatus.OPEN;
            } else if ("CERRADA".equalsIgnoreCase(status)) {
                callStatus = CallStatus.CLOSED;
            } else if ("FINALIZADA".equalsIgnoreCase(status)) {
                callStatus = CallStatus.FINISHED;
            } else {
                callStatus = CallStatus.valueOf(status.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleValidationException("Invalid status value: " + status);
        }

        call.setStatus(callStatus);
        ResearchCall updatedCall = saveCallPort.save(call);
        return mapToResponse(updatedCall);
    }

    private CallResponse mapToResponse(ResearchCall call) {
        String statusName = "ABIERTA";
        if (call.getStatus() == CallStatus.CLOSED) {
            statusName = "CERRADA";
        } else if (call.getStatus() == CallStatus.FINISHED) {
            statusName = "FINALIZADA";
        }

        return new CallResponse(
                call.getId(),
                call.getTitle(),
                call.getStartDate(),
                call.getEndDate(),
                statusName
        );
    }
}