package pe.unas.fiis.sgifiis.thesis.application.dto;

import jakarta.validation.constraints.NotNull;

public record SubsanarPlanTesisCommand(
        @NotNull Integer idEstudiante,
        Integer idDocumentoActual,
        String resumenSubsanado,
        String comentarioSubsanacion
) {}
