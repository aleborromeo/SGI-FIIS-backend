package com.sgi.fiis.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByDni(String dni);
    Optional<UserEntity> findByInstitutionalEmail(String institutionalEmail);
    boolean existsByDni(String dni);
    boolean existsByInstitutionalEmail(String institutionalEmail);

    @Query("SELECT u FROM UserEntity u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.institutionalEmail) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "u.dni LIKE CONCAT('%', :query, '%') OR " +
           "LOWER(u.role.roleCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<UserEntity> search(@Param("query") String query);
}
