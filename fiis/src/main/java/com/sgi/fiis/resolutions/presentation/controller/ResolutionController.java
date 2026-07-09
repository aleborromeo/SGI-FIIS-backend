package com.sgi.fiis.resolutions.presentation.controller;

import com.sgi.fiis.resolutions.application.dto.ResolutionResponseDTO;
import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.domain.port.in.IssueResolutionCommand;
import com.sgi.fiis.resolutions.domain.port.in.IssueResolutionUseCase;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resolutions")
public class ResolutionController {

    private static final String MESSAGE_KEY = "message";

    private final IssueResolutionUseCase issueResolutionUseCase;
    private final MessageSource messageSource;

    public ResolutionController(IssueResolutionUseCase issueResolutionUseCase,
                                MessageSource messageSource) {
        this.issueResolutionUseCase = issueResolutionUseCase;
        this.messageSource = messageSource;
    }

    @PostMapping
    @PreAuthorize("hasRole('DECANO')")
    public ResponseEntity<Map<String, Object>> issueResolution(
            @RequestParam("numeroResolucion") String numeroResolucion,
            @RequestParam("fechaEmision") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEmision,
            @RequestParam("asunto") String asunto,
            @RequestParam("idTramite") Long idTramite,
            @RequestPart("archivo") MultipartFile archivo) throws IOException {

        IssueResolutionCommand command = new IssueResolutionCommand(
                numeroResolucion,
                fechaEmision,
                asunto,
                idTramite,
                archivo.getBytes(),
                archivo.getOriginalFilename(),
                archivo.getContentType()
        );

        Resolution resolution = issueResolutionUseCase.issue(command);

        ResolutionResponseDTO responseDTO = new ResolutionResponseDTO(
                resolution.idResolucion(),
                resolution.numeroResolucion(),
                resolution.fechaEmision(),
                resolution.asunto(),
                resolution.idTramite(),
                resolution.idDocumentoAdjunto(),
                resolution.fechaRegistro()
        );

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("resolution.issue.success", null, locale);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(MESSAGE_KEY, message, "data", responseDTO)
        );
    }
}
