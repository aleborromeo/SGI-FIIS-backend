package com.sgi.fiis.lineas_investigacion.presentation.controller;

import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineResponseDto;
import com.sgi.fiis.lineas_investigacion.application.usecase.ChangeResearchLineStatusUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListResearchLinesUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.GetResearchLineUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.RegisterResearchLineUseCase;
import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.ResearchLineMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/research-lines")
public class ResearchLineController {

    private final RegisterResearchLineUseCase registerResearchLineUseCase;
    private final ListResearchLinesUseCase listResearchLinesUseCase;
    private final GetResearchLineUseCase getResearchLineUseCase;
    private final ChangeResearchLineStatusUseCase changeResearchLineStatusUseCase;
    private final ResearchLineMapper mapper;

    public ResearchLineController(RegisterResearchLineUseCase registerResearchLineUseCase,
                                  ListResearchLinesUseCase listResearchLinesUseCase,
                                  GetResearchLineUseCase getResearchLineUseCase,
                                  ChangeResearchLineStatusUseCase changeResearchLineStatusUseCase,
                                  ResearchLineMapper mapper) {
        this.registerResearchLineUseCase = registerResearchLineUseCase;
        this.listResearchLinesUseCase = listResearchLinesUseCase;
        this.getResearchLineUseCase = getResearchLineUseCase;
        this.changeResearchLineStatusUseCase = changeResearchLineStatusUseCase;
        this.mapper = mapper;
    }

    /** RF-24: Register research line */
    @PostMapping
    public ResponseEntity<ResearchLineResponseDto> register(
            @Valid @RequestBody ResearchLineRequestDto dto) {
        ResearchLine line = registerResearchLineUseCase.execute(mapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDto(line));
    }

    /** RF-25, RF-27, RF-28: List research lines - all or only active for forms */
    @GetMapping
    public ResponseEntity<List<ResearchLineResponseDto>> list(
            @RequestParam(defaultValue = "false") boolean onlyActive) {
        List<ResearchLineResponseDto> lines = listResearchLinesUseCase.execute(onlyActive).stream()
                .map(mapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(lines);
    }

    /** Get line by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ResearchLineResponseDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(mapper.toResponseDto(getResearchLineUseCase.execute(id)));
    }

    /** RF-27: Activate or deactivate research line */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ResearchLineResponseDto> changeStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> body) {
        boolean active = body.getOrDefault("active", true);
        ResearchLine line = changeResearchLineStatusUseCase.execute(id, active);
        return ResponseEntity.ok(mapper.toResponseDto(line));
    }
}
