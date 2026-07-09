package com.sgi.fiis.convocatorias.application.ports.in;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;

public interface UpdateCallStatusUseCase {
    CallResponse updateStatus(Integer id, String status);
}
