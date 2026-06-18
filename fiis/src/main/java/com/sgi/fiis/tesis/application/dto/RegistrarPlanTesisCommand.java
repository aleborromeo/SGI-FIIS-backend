package pe.unas.fiis.sgifiis.thesis.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarPlanTesisCommand(
        @NotBlank @Size(max = 500) String tituloTesis,
        String resumen,
        @NotNull Integer idEstudiante,
        @NotNull Integer idLinea,
        @NotNull Integer idGrupo,
        Integer idDocumentoActual
) {}
