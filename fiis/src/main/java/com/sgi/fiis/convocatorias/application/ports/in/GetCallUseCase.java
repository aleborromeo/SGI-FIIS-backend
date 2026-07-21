package com.sgi.fiis.convocatorias.application.ports.in;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import java.util.List;

public interface GetCallUseCase {
    List<CallResponse> getCalls(String status);
    CallResponse getCallById(Integer id);
    List<CallResponse> getVigentCalls();
}
