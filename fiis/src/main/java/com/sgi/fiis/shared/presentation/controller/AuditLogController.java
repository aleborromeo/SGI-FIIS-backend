package com.sgi.fiis.shared.presentation.controller;

import com.sgi.fiis.shared.application.dto.AuditLogEntryDTO;
import com.sgi.fiis.shared.infrastructure.persistence.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION', 'DECANO')")
    public ResponseEntity<List<AuditLogEntryDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditLogRepository.findAll(page, size));
    }

    @GetMapping("/tabla/{tabla}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION', 'DECANO')")
    public ResponseEntity<List<AuditLogEntryDTO>> findByTabla(
            @PathVariable String tabla,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditLogRepository.findByTabla(tabla, page, size));
    }

    @GetMapping("/registro/{idRegistro}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION', 'DECANO')")
    public ResponseEntity<List<AuditLogEntryDTO>> findByIdRegistro(
            @PathVariable Long idRegistro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditLogRepository.findByIdRegistro(idRegistro, page, size));
    }
}
