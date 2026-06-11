package com.sgi.fiis.users.presentation.controller;

import com.sgi.fiis.users.application.dto.RolResponseDto;
import com.sgi.fiis.users.domain.port.RolRepositoryPort;
import com.sgi.fiis.users.presentation.mapper.RolMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    private final RolRepositoryPort rolRepository;
    private final RolMapper rolMapper;

    public RolController(RolRepositoryPort rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    @GetMapping
    public ResponseEntity<List<RolResponseDto>> listarRoles() {
        List<RolResponseDto> response = rolRepository.findAll().stream()
                .map(rolMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
