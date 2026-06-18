package com.sgi.fiis.convocatorias.application.ports.out;

import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import java.util.List;
import java.util.Optional;

public interface SaveCallPort {
    ResearchCall save(ResearchCall researchCall);
    Optional<ResearchCall> findById(Integer id);
    List<ResearchCall> findByStatus(CallStatus status);
    List<ResearchCall> findAll();
    boolean areLinesActive(List<Integer> lineIds);
}
