package com.sgi.fiis.users.presentation.controller;

import com.sgi.fiis.users.application.dto.RoleResponseDto;
import com.sgi.fiis.users.domain.port.RoleRepositoryPort;
import com.sgi.fiis.users.presentation.mapper.RoleMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleRepositoryPort roleRepository;
    private final RoleMapper roleMapper;

    public RoleController(RoleRepositoryPort roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> listRoles() {
        List<RoleResponseDto> response = roleRepository.findAll().stream()
                .map(roleMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }
}
