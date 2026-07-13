package com.sgi.fiis.convocatorias.application.ports.in;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;

public interface CreateCallUseCase {
    CallResponse execute(CreateCallRequest request, Integer creatorId);
}
