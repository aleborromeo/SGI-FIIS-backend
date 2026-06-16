package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.RolRepositoryPort;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Caso de uso: Editar datos de un usuario (RF-08).
 */
@Service
public class EditarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final RolRepositoryPort rolRepository;

    public EditarUsuarioUseCase(UsuarioRepositoryPort usuarioRepository,
                                RolRepositoryPort rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Transactional
    public Usuario execute(Long id, String nombres, String apellidos,
                           String correo, String telefono, String rolCodigo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (nombres != null && !nombres.isBlank()) {
            usuario.setNombres(nombres);
        }
        if (apellidos != null && !apellidos.isBlank()) {
            usuario.setApellidos(apellidos);
        }
        if (correo != null && !correo.isBlank()) {
            if (!correo.toLowerCase().endsWith(".edu.pe")) {
                throw new BusinessException("El correo institucional debe pertenecer al dominio .edu.pe");
            }
            // Verificar que no exista otro usuario con ese correo
            usuarioRepository.findByCorreo(correo).ifPresent(existente -> {
                if (!existente.getId().equals(id)) {
                    throw new DuplicateResourceException("Usuario", "correo", correo);
                }
            });
            usuario.setCorreoInstitucional(correo);
        }
        if (telefono != null) {
            usuario.setTelefono(telefono);
        }
        if (rolCodigo != null && !rolCodigo.isBlank()) {
            rolRepository.findByCodigo(rolCodigo)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", "codigo", rolCodigo));
            usuario.setRolCodigo(rolCodigo);
        }

        usuario.setFechaActualizacion(LocalDateTime.now(java.time.ZoneId.systemDefault()));
        return usuarioRepository.save(usuario);
    }
}
