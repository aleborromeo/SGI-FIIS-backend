package pe.unas.fiis.sgifiis.thesis.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegistrarResolucionCommand(
        @NotBlank String numeroResolucion,
        @NotNull LocalDate fechaEmision,
        @NotBlank String asunto,
        Integer idDocumentoAdjunto
) {}
