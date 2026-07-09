package com.sgi.fiis.thesis.application.dto;

import java.time.LocalDateTime;
import com.sgi.fiis.thesis.domain.ThesisReportStatus;

public record ThesisReportResponse(
        Integer idInformeTesis,
        Integer idPlanTesis,
        String tituloFinal,
        Integer idDocumentoTesis,
        LocalDateTime fechaPresentacion,
        ThesisReportStatus estadoInforme
) {}
