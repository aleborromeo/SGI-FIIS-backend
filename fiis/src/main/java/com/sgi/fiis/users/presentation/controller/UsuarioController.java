package com.sgi.fiis.users.presentation.controller;

import com.sgi.fiis.users.application.dto.UsuarioRequestDto;
import com.sgi.fiis.users.application.dto.UsuarioResponseDto;
import com.sgi.fiis.users.application.dto.UsuarioUpdateDto;
import com.sgi.fiis.users.application.usecase.*;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.presentation.mapper.UsuarioMapper;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final EditarUsuarioUseCase editarUsuarioUseCase;
    private final CambiarEstadoUsuarioUseCase cambiarEstadoUseCase;
    private final ReiniciarPasswordUseCase reiniciarPasswordUseCase;
    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private final UsuarioMapper mapper;

    public UsuarioController(CrearUsuarioUseCase crearUsuarioUseCase,
                             EditarUsuarioUseCase editarUsuarioUseCase,
                             CambiarEstadoUsuarioUseCase cambiarEstadoUseCase,
                             ReiniciarPasswordUseCase reiniciarPasswordUseCase,
                             ListarUsuariosUseCase listarUsuariosUseCase,
                             ObtenerUsuarioUseCase obtenerUsuarioUseCase,
                             UsuarioMapper mapper) {
        this.crearUsuarioUseCase = crearUsuarioUseCase;
        this.editarUsuarioUseCase = editarUsuarioUseCase;
        this.cambiarEstadoUseCase = cambiarEstadoUseCase;
        this.reiniciarPasswordUseCase = reiniciarPasswordUseCase;
        this.listarUsuariosUseCase = listarUsuariosUseCase;
        this.obtenerUsuarioUseCase = obtenerUsuarioUseCase;
        this.mapper = mapper;
    }

    /** RF-07: Registrar usuario */
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> crear(@Valid @RequestBody UsuarioRequestDto dto) {
        Usuario usuario = mapper.toDomain(dto);
        Usuario creado = crearUsuarioUseCase.execute(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDto(creado));
    }

    /** RF-08: Editar usuario */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> editar(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioUpdateDto dto) {
        Usuario actualizado = editarUsuarioUseCase.execute(
                id, dto.getNombres(), dto.getApellidos(),
                dto.getCorreoInstitucional(), dto.getTelefono(), dto.getRolCodigo()
        );
        return ResponseEntity.ok(mapper.toResponseDto(actualizado));
    }

    /** RF-14: Listar y buscar usuarios */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listar(
            @RequestParam(required = false) String query) {
        List<UsuarioResponseDto> usuarios = listarUsuariosUseCase.execute(query).stream()
                .map(mapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    /** Obtener usuario por ID */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> obtener(@PathVariable Long id) {
        Usuario usuario = obtenerUsuarioUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponseDto(usuario));
    }

    /** RF-11: Activar o desactivar usuario */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDto> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        boolean activar = body.getOrDefault("activo", true);
        Long idAutenticado = 0L;
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            idAutenticado = customUserDetails.getId();
        }
        Usuario actualizado = cambiarEstadoUseCase.execute(id, activar, idAutenticado);
        return ResponseEntity.ok(mapper.toResponseDto(actualizado));
    }

    /** RF-12: Reiniciar contraseña */
    @PatchMapping("/{id}/reiniciar-password")
    public ResponseEntity<Map<String, String>> reiniciarPassword(@PathVariable Long id) {
        reiniciarPasswordUseCase.execute(id);
        return ResponseEntity.ok(Map.of("message", "Contraseña reiniciada exitosamente"));
    }
}
