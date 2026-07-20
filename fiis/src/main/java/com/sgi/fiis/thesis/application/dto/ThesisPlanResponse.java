package com.sgi.fiis.thesis.application.dto;

import java.time.LocalDateTime;
import com.sgi.fiis.thesis.domain.ThesisPlanStatus;
import com.sgi.fiis.thesis.domain.ThesisProcedureStatus;
import com.sgi.fiis.thesis.domain.ReviewerRole;

public record ThesisPlanResponse(
        Integer idPlanTesis,
        String tituloTesis,
        String resumen,
        Long idEstudiante,
        Integer idLinea,
        Integer idGrupo,
        Integer idDocumentoActual,
        ThesisPlanStatus estadoPlan,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        Integer idTramite,
        ThesisProcedureStatus estadoTramite,
        ReviewerRole revisorActual,
        String nombreEstudiante,
        String apellidoEstudiante,
        String nombreGrupo,
        String codigoGrupo,
        String nombreLinea,
        String nombreDocumento,
        String observacionActual
) {}
