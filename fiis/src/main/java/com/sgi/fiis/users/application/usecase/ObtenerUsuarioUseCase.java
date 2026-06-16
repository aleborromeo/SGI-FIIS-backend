package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Obtener un usuario por ID.
 */
@Service
public class ObtenerUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public ObtenerUsuarioUseCase(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario execute(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }
}
