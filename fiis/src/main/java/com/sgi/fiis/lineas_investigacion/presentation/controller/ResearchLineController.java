package com.sgi.fiis.lineas_investigacion.presentation.controller;

import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineResponseDto;
import com.sgi.fiis.lineas_investigacion.application.usecase.ChangeResearchLineStatusUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListResearchLinesUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.GetResearchLineUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.RegisterResearchLineUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.AssignGroupToResearchLineUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.RemoveGroupFromResearchLineUseCase;
import com.sgi.fiis.grupos_investigacion.application.usecase.ListResearchGroupsByLineUseCase;
import com.sgi.fiis.grupos_investigacion.application.dto.ResearchGroupResponseDto;
import com.sgi.fiis.grupos_investigacion.presentation.mapper.ResearchGroupMapper;
import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.ResearchLineMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/research-lines")
@RequiredArgsConstructor
public class ResearchLineController {

    private final RegisterResearchLineUseCase registerResearchLineUseCase;
    private final ListResearchLinesUseCase listResearchLinesUseCase;
    private final GetResearchLineUseCase getResearchLineUseCase;
    private final ChangeResearchLineStatusUseCase changeResearchLineStatusUseCase;
    private final AssignGroupToResearchLineUseCase assignGroupToResearchLineUseCase;
    private final RemoveGroupFromResearchLineUseCase removeGroupFromResearchLineUseCase;
    private final ListResearchGroupsByLineUseCase listResearchGroupsByLineUseCase;
    private final ResearchLineMapper mapper;
    private final ResearchGroupMapper groupMapper;

    /** RF-24: Register research line */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResearchLineResponseDto> register(
            @Valid @RequestBody ResearchLineRequestDto dto) {
        ResearchLine line = registerResearchLineUseCase.execute(mapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDto(line));
    }

    /** RF-25, RF-27, RF-28: List research lines - all or only active for forms */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ResearchLineResponseDto>> list(
            @RequestParam(defaultValue = "false") boolean onlyActive) {
        List<ResearchLineResponseDto> lines = listResearchLinesUseCase.execute(onlyActive).stream()
                .map(mapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(lines);
    }

    /** Get line by ID */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResearchLineResponseDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(mapper.toResponseDto(getResearchLineUseCase.execute(id)));
    }

    /** RF-27: Activate or deactivate research line */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResearchLineResponseDto> changeStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> body) {
        boolean active = body.getOrDefault("active", true);
        ResearchLine line = changeResearchLineStatusUseCase.execute(id, active);
        return ResponseEntity.ok(mapper.toResponseDto(line));
    }

    /** RF-26: List research groups associated to a line */
    @GetMapping("/{id}/groups")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ResearchGroupResponseDto>> listGroups(@PathVariable Integer id) {
        getResearchLineUseCase.execute(id); // validates line exists
        return ResponseEntity.ok(listResearchGroupsByLineUseCase.execute(id).stream()
                .map(groupMapper::toResponseDto)
                .toList());
    }

    /** Assign group to research line */
    @PostMapping("/{id}/groups/{groupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignGroup(
            @PathVariable Integer id,
            @PathVariable Integer groupId) {
        assignGroupToResearchLineUseCase.execute(id, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** Remove group from research line */
    @DeleteMapping("/{id}/groups/{groupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeGroup(
            @PathVariable Integer id,
            @PathVariable Integer groupId) {
        removeGroupFromResearchLineUseCase.execute(id, groupId);
        return ResponseEntity.noContent().build();
    }
}
