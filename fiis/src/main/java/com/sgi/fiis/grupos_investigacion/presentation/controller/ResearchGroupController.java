package com.sgi.fiis.grupos_investigacion.presentation.controller;

import com.sgi.fiis.grupos_investigacion.application.dto.*;
import com.sgi.fiis.grupos_investigacion.application.usecase.*;
import com.sgi.fiis.grupos_investigacion.presentation.mapper.ResearchGroupMapper;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListResearchLinesByGroupUseCase;
import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineResponseDto;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.ResearchLineMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/research-groups")
@lombok.RequiredArgsConstructor
public class ResearchGroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final ListGroupsUseCase listGroupsUseCase;
    private final GetGroupUseCase getGroupUseCase;
    private final AssignCoordinatorUseCase assignCoordinatorUseCase;
    private final AssignMemberUseCase assignMemberUseCase;
    private final RemoveMemberUseCase removeMemberUseCase;
    private final ListMembersUseCase listMembersUseCase;
    private final ListResearchLinesByGroupUseCase listResearchLinesByGroupUseCase;
    private final ResearchGroupMapper mapper;
    private final ResearchLineMapper lineMapper;
    private final JdbcTemplate jdbcTemplate;

    /** Create research group */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResearchGroupResponseDto> create(
            @Valid @RequestBody ResearchGroupRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDto(createGroupUseCase.execute(mapper.toDomain(dto))));
    }

    /** RF-15: List research groups */
    @GetMapping
    public ResponseEntity<List<ResearchGroupResponseDto>> list() {
        return ResponseEntity.ok(listGroupsUseCase.execute().stream()
                .map(mapper::toResponseDto)
                .toList());
    }

    /** Get group by ID */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResearchGroupResponseDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(mapper.toResponseDto(getGroupUseCase.execute(id)));
    }

    /** RF-18: Assign coordinator to a group */
    @PatchMapping("/{id}/coordinator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResearchGroupResponseDto> assignCoordinator(
            @PathVariable Integer id,
            @Valid @RequestBody AssignCoordinatorRequestDto dto) {
        return ResponseEntity.ok(mapper.toResponseDto(
                assignCoordinatorUseCase.execute(id, dto.getUserId())));
    }

    /** RF-19: Register active membership */
    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponseDto> assignMember(
            @PathVariable Integer id,
            @Valid @RequestBody AssignMemberRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toMembershipResponseDto(
                        assignMemberUseCase.execute(id, dto.getUserId())));
    }

    /** RF-20: Remove member from group (soft delete) */
    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipResponseDto> removeMember(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        return ResponseEntity.ok(mapper.toMembershipResponseDto(
                removeMemberUseCase.execute(id, userId)));
    }

    /** RF-22: List active members of the group */
    @GetMapping("/{id}/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MembershipResponseDto>> listMembers(@PathVariable Integer id) {
        return ResponseEntity.ok(listMembersUseCase.execute(id).stream()
                .map(mapper::toMembershipResponseDto)
                .toList());
    }

    /** RF-24: List research lines associated to a group */
    @GetMapping("/{id}/lines")
    public ResponseEntity<List<ResearchLineResponseDto>> listLines(@PathVariable Integer id) {
        getGroupUseCase.execute(id); // validates group exists
        return ResponseEntity.ok(listResearchLinesByGroupUseCase.execute(id).stream()
                .map(lineMapper::toResponseDto)
                .toList());
    }

    /** Users without active group membership (for assigning to groups) */
    @GetMapping("/available-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAvailableUsers() {
        String sql = """
                SELECT u.id_usuario AS id,
                       u.nombres AS "firstNames",
                       u.apellidos AS "lastNames",
                       u.correo_institucional AS "institutionalEmail"
                FROM usuarios u
                WHERE u.es_activo = TRUE
                  AND NOT EXISTS (
                    SELECT 1 FROM membresias_grupo m
                    WHERE m.id_usuario = u.id_usuario AND m.es_activo = TRUE
                  )
                ORDER BY u.nombres, u.apellidos
                """;
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(users);
    }

    /** Users with COORDINADOR_GRUPO role (for coordinator assignment dropdown) */
    @GetMapping("/coordinator-candidates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getCoordinatorCandidates() {
        String sql = """
                SELECT u.id_usuario AS id,
                       u.nombres AS "firstNames",
                       u.apellidos AS "lastNames",
                       u.correo_institucional AS "institutionalEmail"
                FROM usuarios u
                WHERE u.es_activo = TRUE
                  AND (
                    EXISTS (SELECT 1 FROM roles r WHERE r.id_rol = u.id_rol_principal AND r.codigo_rol = 'COORDINADOR_GRUPO')
                    OR EXISTS (SELECT 1 FROM usuarios_roles ur
                               JOIN roles r ON ur.id_rol = r.id_rol
                               WHERE ur.id_usuario = u.id_usuario AND r.codigo_rol = 'COORDINADOR_GRUPO')
                  )
                ORDER BY u.nombres, u.apellidos
                """;
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(users);
    }

    /** Find active group of a user */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResearchGroupResponseDto> getGroupByUser(@PathVariable Integer userId) {
        String sql = """
                SELECT g.id_grupo, g.codigo_grupo, g.nombre_grupo,
                       g.id_coordinador_actual, g.es_activo, g.fecha_creacion
                FROM membresias_grupo m
                JOIN grupos_investigacion g ON m.id_grupo = g.id_grupo
                WHERE m.id_usuario = ? AND m.es_activo = TRUE AND g.es_activo = TRUE
                LIMIT 1
                """;
        List<com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup> groups = jdbcTemplate.query(sql, (rs, rowNum) -> com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup.builder()
                .id(rs.getInt("id_grupo"))
                .groupCode(rs.getString("codigo_grupo"))
                .groupName(rs.getString("nombre_grupo"))
                .currentCoordinatorId(rs.getObject("id_coordinador_actual") != null
                        ? rs.getInt("id_coordinador_actual") : null)
                .active(rs.getBoolean("es_activo"))
                .createdAt(rs.getTimestamp("fecha_creacion") != null
                        ? rs.getTimestamp("fecha_creacion").toLocalDateTime() : null)
                .build(), userId);
        if (groups.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(mapper.toResponseDto(groups.get(0)));
    }
}
