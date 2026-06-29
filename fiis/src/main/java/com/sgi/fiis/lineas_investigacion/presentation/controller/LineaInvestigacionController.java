package com.sgi.fiis.lineas_investigacion.presentation.controller;

import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionResponseDto;
import com.sgi.fiis.lineas_investigacion.application.usecase.CambiarEstadoLineaUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListarLineasUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.ObtenerLineaUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.RegistrarLineaUseCase;
import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.LineaInvestigacionMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lineas-investigacion")
public class LineaInvestigacionController {

    private final RegistrarLineaUseCase registrarLineaUseCase;
    private final ListarLineasUseCase listarLineasUseCase;
    private final ObtenerLineaUseCase obtenerLineaUseCase;
    private final CambiarEstadoLineaUseCase cambiarEstadoLineaUseCase;
    private final LineaInvestigacionMapper mapper;

    public LineaInvestigacionController(RegistrarLineaUseCase registrarLineaUseCase,
                                        ListarLineasUseCase listarLineasUseCase,
                                        ObtenerLineaUseCase obtenerLineaUseCase,
                                        CambiarEstadoLineaUseCase cambiarEstadoLineaUseCase,
                                        LineaInvestigacionMapper mapper) {
        this.registrarLineaUseCase = registrarLineaUseCase;
        this.listarLineasUseCase = listarLineasUseCase;
        this.obtenerLineaUseCase = obtenerLineaUseCase;
        this.cambiarEstadoLineaUseCase = cambiarEstadoLineaUseCase;
        this.mapper = mapper;
    }

    /** RF-24: Registrar línea de investigación */
    @PostMapping
    public ResponseEntity<LineaInvestigacionResponseDto> registrar(
            @Valid @RequestBody LineaInvestigacionRequestDto dto) {
        LineaInvestigacion linea = registrarLineaUseCase.execute(mapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDto(linea));
    }

    /** RF-25, RF-27, RF-28: Listar líneas — todas o solo activas para formularios */
    @GetMapping
    public ResponseEntity<List<LineaInvestigacionResponseDto>> listar(
            @RequestParam(defaultValue = "false") boolean soloActivas) {
        List<LineaInvestigacionResponseDto> lineas = listarLineasUseCase.execute(soloActivas).stream()
                .map(mapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(lineas);
    }

    /** Obtener línea por ID */
    @GetMapping("/{id}")
    public ResponseEntity<LineaInvestigacionResponseDto> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(mapper.toResponseDto(obtenerLineaUseCase.execute(id)));
    }

    /** RF-27: Activar o desactivar línea de investigación */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<LineaInvestigacionResponseDto> cambiarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> body) {
        boolean activar = body.getOrDefault("esActiva", true);
        LineaInvestigacion linea = cambiarEstadoLineaUseCase.execute(id, activar);
        return ResponseEntity.ok(mapper.toResponseDto(linea));
    }
}
