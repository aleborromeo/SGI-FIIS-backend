package pe.unas.fiis.sgifiis.thesis.application.dto;

import java.time.LocalDateTime;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoInformeTesis;

public record InformeTesisResponse(
        Integer idInformeTesis,
        Integer idPlanTesis,
        String tituloFinal,
        Integer idDocumentoTesis,
        LocalDateTime fechaPresentacion,
        EstadoInformeTesis estadoInforme
) {}
