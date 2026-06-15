package com.sgi.fiis.users.domain.port;

import com.sgi.fiis.users.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del dominio para operaciones de persistencia de Usuarios.
 */
public interface UsuarioRepositoryPort {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByCorreo(String correoInstitucional);
    List<Usuario> findAll();
    List<Usuario> search(String query);
    boolean existsByDni(String dni);
    boolean existsByCorreo(String correoInstitucional);
}
