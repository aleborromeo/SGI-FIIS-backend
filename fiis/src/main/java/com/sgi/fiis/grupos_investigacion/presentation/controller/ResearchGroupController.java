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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /** RF-22: Create research group */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResearchGroupResponseDto> create(
            @Valid @RequestBody ResearchGroupRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDto(createGroupUseCase.execute(mapper.toDomain(dto))));
    }

    /** RF-23: List research groups */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
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

    /** RF-19: Assign coordinator to a group */
    @PatchMapping("/{id}/coordinator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResearchGroupResponseDto> assignCoordinator(
            @PathVariable Integer id,
            @Valid @RequestBody AssignCoordinatorRequestDto dto) {
        return ResponseEntity.ok(mapper.toResponseDto(
                assignCoordinatorUseCase.execute(id, dto.getUserId())));
    }

    /** RF-20: Add member to group */
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

    /** RF-20: List active members of the group */
    @GetMapping("/{id}/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MembershipResponseDto>> listMembers(@PathVariable Integer id) {
        return ResponseEntity.ok(listMembersUseCase.execute(id).stream()
                .map(mapper::toMembershipResponseDto)
                .toList());
    }

    /** RF-26: List research lines associated to a group */
    @GetMapping("/{id}/lines")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ResearchLineResponseDto>> listLines(@PathVariable Integer id) {
        getGroupUseCase.execute(id); // validates group exists
        return ResponseEntity.ok(listResearchLinesByGroupUseCase.execute(id).stream()
                .map(lineMapper::toResponseDto)
                .toList());
    }
}
