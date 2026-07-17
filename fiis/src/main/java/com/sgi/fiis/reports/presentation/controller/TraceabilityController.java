package com.sgi.fiis.reports.presentation.controller;

import com.sgi.fiis.reports.application.service.TraceabilityService;
import com.sgi.fiis.reports.domain.model.ProcedureRecentActivity;
import com.sgi.fiis.reports.domain.model.TraceabilityMovement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("isAuthenticated()")
public class TraceabilityController {

    private final TraceabilityService service;
    private final JdbcTemplate jdbcTemplate;

    public TraceabilityController(TraceabilityService service, JdbcTemplate jdbcTemplate) {
        this.service = service;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/traceability/{procedureId}")
    public ResponseEntity<?> getTraceability(
            @PathVariable Integer procedureId) {

        Integer userGroupId = resolveUserGroupId();
        try {
            List<TraceabilityMovement> history = service.getTraceability(procedureId, userGroupId);
            return ResponseEntity.ok(history);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/traceability/recent")
    public ResponseEntity<List<ProcedureRecentActivity>> getRecentActivity(
            @RequestParam(defaultValue = "7") int days) {

        Integer userGroupId = resolveUserGroupId();
        List<ProcedureRecentActivity> activities = service.getProceduresWithRecentActivity(userGroupId, days);
        return ResponseEntity.ok(activities);
    }

    private Integer resolveUserGroupId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return null;
        }

        boolean isCoordinator = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDINADOR_GRUPO"));
        if (!isCoordinator) {
            return null;
        }

        String sql = "SELECT id_grupo FROM grupos_investigacion WHERE id_coordinador_actual = ?";
        List<Integer> groups = jdbcTemplate.queryForList(sql, Integer.class, userDetails.getId());
        return groups.isEmpty() ? null : groups.get(0);
    }
}
