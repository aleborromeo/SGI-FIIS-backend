package com.sgi.fiis.reportes_progresivos.presentation.controller;

import com.sgi.fiis.reportes_progresivos.application.dto.AmendReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.CreateReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.port.in.AmendProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.in.CreateProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.in.QueryProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.in.ReviewProgressReportUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Progress Reports.
 */
@RestController
@RequestMapping("/api/progress-reports")
public class ProgressReportController {

    private final CreateProgressReportUseCase createUseCase;
    private final ReviewProgressReportUseCase reviewUseCase;
    private final QueryProgressReportUseCase  queryUseCase;
    private final AmendProgressReportUseCase  amendUseCase;

    public ProgressReportController(CreateProgressReportUseCase createUseCase,
                                    ReviewProgressReportUseCase reviewUseCase,
                                    QueryProgressReportUseCase queryUseCase,
                                    AmendProgressReportUseCase amendUseCase) {
        this.createUseCase = createUseCase;
        this.reviewUseCase = reviewUseCase;
        this.queryUseCase  = queryUseCase;
        this.amendUseCase  = amendUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCENTE_INVESTIGADOR', 'ESTUDIANTE')")
    public ResponseEntity<ProgressReportResponse> create(@RequestBody CreateReportCommand command) {
        ProgressReportResponse response = createUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProgressReportResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(queryUseCase.getById(id));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProgressReportResponse>> listByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(queryUseCase.listByProject(projectId));
    }

    @PatchMapping("/{id}/forward")
    @PreAuthorize("hasAnyRole('DOCENTE_INVESTIGADOR', 'ESTUDIANTE')")
    public ResponseEntity<ProgressReportResponse> forward(@PathVariable Long id) {
        return ResponseEntity.ok(reviewUseCase.forwardToDirector(id));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('DIRECTOR_INVESTIGACION', 'COORDINADOR_GRUPO')")
    public ResponseEntity<ProgressReportResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(reviewUseCase.approve(id));
    }

    @PatchMapping("/{id}/observe")
    @PreAuthorize("hasAnyRole('DIRECTOR_INVESTIGACION', 'COORDINADOR_GRUPO')")
    public ResponseEntity<ProgressReportResponse> observe(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String observation = body.getOrDefault("observation", "");
        return ResponseEntity.ok(reviewUseCase.observe(id, observation));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('DIRECTOR_INVESTIGACION', 'COORDINADOR_GRUPO')")
    public ResponseEntity<ProgressReportResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(reviewUseCase.reject(id));
    }

    @PatchMapping("/{id}/amend")
    @PreAuthorize("hasAnyRole('DOCENTE_INVESTIGADOR', 'ESTUDIANTE')")
    public ResponseEntity<ProgressReportResponse> amend(
            @PathVariable Long id,
            @RequestBody AmendReportCommand command) {
        command.setReportId(id);
        return ResponseEntity.ok(amendUseCase.amend(command));
    }
}
