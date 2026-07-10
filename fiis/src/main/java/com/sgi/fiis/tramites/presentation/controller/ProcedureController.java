package com.sgi.fiis.tramites.presentation.controller;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.tramites.application.dto.*;
import com.sgi.fiis.tramites.application.usecase.*;
import com.sgi.fiis.users.domain.model.RoleEnum;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/procedures")
public class ProcedureController {

    private final CreateProcedureUseCase createProcedureUseCase;
    private final ApproveProcedureUseCase approveProcedureUseCase;
    private final FlagProcedureUseCase flagProcedureUseCase;
    private final RemediateProcedureUseCase remediateProcedureUseCase;
    private final RejectProcedureUseCase rejectProcedureUseCase;
    private final RegisterResolutionUseCase registerResolutionUseCase;
    private final GetTraceabilityUseCase getTraceabilityUseCase;

    public ProcedureController(CreateProcedureUseCase createProcedureUseCase,
                               ApproveProcedureUseCase approveProcedureUseCase,
                               FlagProcedureUseCase flagProcedureUseCase,
                               RemediateProcedureUseCase remediateProcedureUseCase,
                               RejectProcedureUseCase rejectProcedureUseCase,
                               RegisterResolutionUseCase registerResolutionUseCase,
                               GetTraceabilityUseCase getTraceabilityUseCase) {
        this.createProcedureUseCase    = createProcedureUseCase;
        this.approveProcedureUseCase   = approveProcedureUseCase;
        this.flagProcedureUseCase      = flagProcedureUseCase;
        this.remediateProcedureUseCase = remediateProcedureUseCase;
        this.rejectProcedureUseCase    = rejectProcedureUseCase;
        this.registerResolutionUseCase = registerResolutionUseCase;
        this.getTraceabilityUseCase    = getTraceabilityUseCase;
    }

    @PostMapping
    public ResponseEntity<ProcedureResponseDto> create(
            @Valid @RequestBody ProcedureRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        dto.setApplicantId(userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createProcedureUseCase.execute(dto));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ProcedureResponseDto> approve(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                approveProcedureUseCase.execute(id, extractRole(userDetails), userDetails.getId()));
    }

    @PutMapping("/{id}/flag")
    public ResponseEntity<ProcedureResponseDto> flag(
            @PathVariable Long id,
            @Valid @RequestBody FlagProcedureRequestDto body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                flagProcedureUseCase.execute(
                        id, extractRole(userDetails), userDetails.getId(), body.getTextoObservacion()));
    }

    @PutMapping("/{id}/remediate")
    public ResponseEntity<ProcedureResponseDto> remediate(
            @PathVariable Long id,
            @Valid @RequestBody RemediateProcedureRequestDto body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                remediateProcedureUseCase.execute(id, userDetails.getId(), body.getDetalleSubsanacion()));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ProcedureResponseDto> reject(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                rejectProcedureUseCase.execute(id, extractRole(userDetails), userDetails.getId()));
    }

    @PutMapping("/{id}/resolution")
    public ResponseEntity<ProcedureResponseDto> registerResolution(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                registerResolutionUseCase.execute(id, userDetails.getId()));
    }

    @GetMapping("/{id}/traceability")
    public ResponseEntity<List<ProcedureMovementResponseDto>> getTraceability(@PathVariable Long id) {
        return ResponseEntity.ok(getTraceabilityUseCase.execute(id));
    }

    private RoleEnum extractRole(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> RoleEnum.valueOf(a.getAuthority().replace("ROLE_", "")))
                .orElseThrow(() -> new BusinessException("Authenticated user has no role assigned"));
    }
}
