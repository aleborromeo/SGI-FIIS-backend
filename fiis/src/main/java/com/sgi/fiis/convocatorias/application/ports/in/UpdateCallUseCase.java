package com.sgi.fiis.convocatorias.application.ports.in;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.UpdateCallRequest;

public interface UpdateCallUseCase {
    CallResponse execute(Integer id, UpdateCallRequest request);
}
