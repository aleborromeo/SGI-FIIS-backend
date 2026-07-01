package com.sgi.fiis.thesis.domain.port.in;

import java.util.List;
import com.sgi.fiis.thesis.application.dto.ThesisReportResponse;
import com.sgi.fiis.thesis.application.dto.RegisterThesisReportCommand;

public interface ThesisReportUseCase {
    ThesisReportResponse registrarInformeFinal(RegisterThesisReportCommand command);
    ThesisReportResponse aprobarInforme(Integer idInformeTesis);
    ThesisReportResponse observarInforme(Integer idInformeTesis, String observacion);
    ThesisReportResponse obtenerPorId(Integer idInformeTesis);
    List<ThesisReportResponse> listarPorPlan(Integer idPlanTesis);
}
