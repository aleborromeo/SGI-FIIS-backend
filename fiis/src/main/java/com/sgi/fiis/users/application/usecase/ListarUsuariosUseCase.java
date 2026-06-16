package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso: Listar y buscar usuarios con filtros (RF-14).
 */
@Service
public class ListarUsuariosUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public ListarUsuariosUseCase(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> execute(String query) {
        if (query == null || query.isBlank()) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.search(query.trim());
    }
}
