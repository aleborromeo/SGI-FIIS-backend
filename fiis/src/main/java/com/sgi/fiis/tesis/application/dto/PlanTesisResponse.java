package pe.unas.fiis.sgifiis.thesis.application.dto;

import java.time.LocalDateTime;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoPlanTesis;

public record PlanTesisResponse(
        Integer idPlanTesis,
        String tituloTesis,
        String resumen,
        Integer idEstudiante,
        Integer idLinea,
        Integer idGrupo,
        Integer idDocumentoActual,
        EstadoPlanTesis estadoPlan,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {}
