package pe.unas.fiis.sgifiis.thesis.application.dto;

import java.time.LocalDateTime;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoPlanTesis;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoTramiteTesis;
import pe.unas.fiis.sgifiis.thesis.domain.RolRevisor;

public record PlanTesisResponse(
        Integer idPlanTesis,
        String tituloTesis,
        String resumen,
        Long idEstudiante,
        Integer idLinea,
        Integer idGrupo,
        Integer idDocumentoActual,
        EstadoPlanTesis estadoPlan,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        Integer idTramite,
        EstadoTramiteTesis estadoTramite,
        RolRevisor revisorActual
) {}
