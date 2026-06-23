package com.sgi.fiis.grupos_investigacion.presentation.controller;

import com.sgi.fiis.grupos_investigacion.application.dto.*;
import com.sgi.fiis.grupos_investigacion.application.usecase.*;
import com.sgi.fiis.grupos_investigacion.presentation.mapper.GrupoInvestigacionMapper;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListarLineasPorGrupoUseCase;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionResponseDto;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.LineaInvestigacionMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grupos-investigacion")
public class GrupoInvestigacionController {

    private final CrearGrupoUseCase crearGrupoUseCase;
    private final ListarGruposUseCase listarGruposUseCase;
    private final ObtenerGrupoUseCase obtenerGrupoUseCase;
    private final AsignarCoordinadorUseCase asignarCoordinadorUseCase;
    private final AsignarMiembroUseCase asignarMiembroUseCase;
    private final RetirarMiembroUseCase retirarMiembroUseCase;
    private final ListarMiembrosUseCase listarMiembrosUseCase;
    private final ListarLineasPorGrupoUseCase listarLineasPorGrupoUseCase;
    private final GrupoInvestigacionMapper mapper;
    private final LineaInvestigacionMapper lineaMapper;

    public GrupoInvestigacionController(CrearGrupoUseCase crearGrupoUseCase,
                                        ListarGruposUseCase listarGruposUseCase,
                                        ObtenerGrupoUseCase obtenerGrupoUseCase,
                                        AsignarCoordinadorUseCase asignarCoordinadorUseCase,
                                        AsignarMiembroUseCase asignarMiembroUseCase,
                                        RetirarMiembroUseCase retirarMiembroUseCase,
                                        ListarMiembrosUseCase listarMiembrosUseCase,
                                        ListarLineasPorGrupoUseCase listarLineasPorGrupoUseCase,
                                        GrupoInvestigacionMapper mapper,
                                        LineaInvestigacionMapper lineaMapper) {
        this.crearGrupoUseCase = crearGrupoUseCase;
        this.listarGruposUseCase = listarGruposUseCase;
        this.obtenerGrupoUseCase = obtenerGrupoUseCase;
        this.asignarCoordinadorUseCase = asignarCoordinadorUseCase;
        this.asignarMiembroUseCase = asignarMiembroUseCase;
        this.retirarMiembroUseCase = retirarMiembroUseCase;
        this.listarMiembrosUseCase = listarMiembrosUseCase;
        this.listarLineasPorGrupoUseCase = listarLineasPorGrupoUseCase;
        this.mapper = mapper;
        this.lineaMapper = lineaMapper;
    }

    /** RF-22: Crear grupo de investigación */
    @PostMapping
    public ResponseEntity<GrupoInvestigacionResponseDto> crear(
            @Valid @RequestBody GrupoInvestigacionRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDto(crearGrupoUseCase.execute(mapper.toDomain(dto))));
    }

    /** RF-23: Listar grupos de investigación */
    @GetMapping
    public ResponseEntity<List<GrupoInvestigacionResponseDto>> listar() {
        return ResponseEntity.ok(listarGruposUseCase.execute().stream()
                .map(mapper::toResponseDto)
                .toList());
    }

    /** Obtener grupo por ID */
    @GetMapping("/{id}")
    public ResponseEntity<GrupoInvestigacionResponseDto> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(mapper.toResponseDto(obtenerGrupoUseCase.execute(id)));
    }

    /** RF-19: Asignar coordinador a un grupo */
    @PatchMapping("/{id}/coordinador")
    public ResponseEntity<GrupoInvestigacionResponseDto> asignarCoordinador(
            @PathVariable Integer id,
            @Valid @RequestBody AsignarCoordinadorRequestDto dto) {
        return ResponseEntity.ok(mapper.toResponseDto(
                asignarCoordinadorUseCase.execute(id, dto.getIdUsuario())));
    }

    /** RF-20: Agregar miembro al grupo */
    @PostMapping("/{id}/miembros")
    public ResponseEntity<MembresiaResponseDto> asignarMiembro(
            @PathVariable Integer id,
            @Valid @RequestBody AsignarMiembroRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toMembresiaResponseDto(
                        asignarMiembroUseCase.execute(id, dto.getIdUsuario())));
    }

    /** RF-20: Retirar miembro del grupo (soft delete) */
    @DeleteMapping("/{id}/miembros/{idUsuario}")
    public ResponseEntity<MembresiaResponseDto> retirarMiembro(
            @PathVariable Integer id,
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(mapper.toMembresiaResponseDto(
                retirarMiembroUseCase.execute(id, idUsuario)));
    }

    /** RF-20: Listar miembros activos del grupo */
    @GetMapping("/{id}/miembros")
    public ResponseEntity<List<MembresiaResponseDto>> listarMiembros(@PathVariable Integer id) {
        return ResponseEntity.ok(listarMiembrosUseCase.execute(id).stream()
                .map(mapper::toMembresiaResponseDto)
                .toList());
    }

    /** RF-26: Listar líneas de investigación asociadas a un grupo */
    @GetMapping("/{id}/lineas")
    public ResponseEntity<List<LineaInvestigacionResponseDto>> listarLineas(@PathVariable Integer id) {
        obtenerGrupoUseCase.execute(id); // validates group exists
        return ResponseEntity.ok(listarLineasPorGrupoUseCase.execute(id).stream()
                .map(lineaMapper::toResponseDto)
                .toList());
    }
}
