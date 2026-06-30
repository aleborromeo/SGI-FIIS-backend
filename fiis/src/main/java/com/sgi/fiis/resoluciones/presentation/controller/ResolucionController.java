package com.sgi.fiis.resoluciones.presentation.controller;

import com.sgi.fiis.resoluciones.application.dto.ResolucionResponseDTO;
import com.sgi.fiis.resoluciones.domain.model.Resolucion;
import com.sgi.fiis.resoluciones.domain.port.in.EmitirResolucionCommand;
import com.sgi.fiis.resoluciones.domain.port.in.EmitirResolucionUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/resoluciones")
public class ResolucionController {

    private final EmitirResolucionUseCase emitirResolucionUseCase;

    public ResolucionController(EmitirResolucionUseCase emitirResolucionUseCase) {
        this.emitirResolucionUseCase = emitirResolucionUseCase;
    }

    @PostMapping
    public ResponseEntity<ResolucionResponseDTO> emitirResolucion(
            @RequestParam("numeroResolucion") String numeroResolucion,
            @RequestParam("fechaEmision") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEmision,
            @RequestParam("asunto") String asunto,
            @RequestParam("idTramite") Long idTramite,
            @RequestPart("archivo") MultipartFile archivo) throws IOException {

        EmitirResolucionCommand command = new EmitirResolucionCommand(
                numeroResolucion,
                fechaEmision,
                asunto,
                idTramite,
                archivo.getBytes(),
                archivo.getOriginalFilename(),
                archivo.getContentType()
        );

        Resolucion resolucion = emitirResolucionUseCase.emitir(command);

        ResolucionResponseDTO response = new ResolucionResponseDTO(
                resolucion.idResolucion(),
                resolucion.numeroResolucion(),
                resolucion.fechaEmision(),
                resolucion.asunto(),
                resolucion.idTramite(),
                resolucion.idDocumentoAdjunto(),
                resolucion.fechaRegistro()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
