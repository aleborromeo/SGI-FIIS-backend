package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Activar o desactivar un usuario (RF-11, RF-13).
 */
@Service
public class CambiarEstadoUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public CambiarEstadoUsuarioUseCase(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario execute(Long id, boolean activar, Long idUsuarioAutenticado) {
        // RF-13: Impedir que el administrador se desactive a sí mismo
        if (id.equals(idUsuarioAutenticado) && !activar) {
            throw new BusinessException("No puedes desactivar tu propia cuenta");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (activar) {
            usuario.activar();
        } else {
            usuario.desactivar();
        }

        return usuarioRepository.save(usuario);
    }
}
