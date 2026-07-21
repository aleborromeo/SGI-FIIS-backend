package com.sgi.fiis.users.domain.port;

import com.sgi.fiis.users.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Domain port for User persistence operations.
 */
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByDni(String dni);
    Optional<User> findByEmail(String institutionalEmail);
    List<User> findAll();
    List<User> search(String query);
    List<User> search(String query, String role, Boolean active);
    boolean existsByDni(String dni);
    boolean existsByEmail(String institutionalEmail);
    List<User> findAllPaged(int page, int size);
    long countAll();
}
