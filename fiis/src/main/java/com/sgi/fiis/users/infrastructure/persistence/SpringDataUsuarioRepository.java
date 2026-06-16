package com.sgi.fiis.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByDni(String dni);
    Optional<UsuarioEntity> findByCorreoInstitucional(String correoInstitucional);
    boolean existsByDni(String dni);
    boolean existsByCorreoInstitucional(String correoInstitucional);

    @Query("SELECT u FROM UsuarioEntity u WHERE " +
           "LOWER(u.nombres) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.correoInstitucional) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "u.dni LIKE CONCAT('%', :query, '%') OR " +
           "LOWER(u.rol.codigoRol) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<UsuarioEntity> search(@Param("query") String query);
}
