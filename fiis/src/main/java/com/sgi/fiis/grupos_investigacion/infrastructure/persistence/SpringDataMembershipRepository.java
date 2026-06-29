package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataMembershipRepository extends JpaRepository<MembershipEntity, Integer> {
    Optional<MembershipEntity> findByUserIdAndGroupIdAndActiveTrue(Integer userId, Integer groupId);
    List<MembershipEntity> findByGroupIdAndActiveTrue(Integer groupId);
    boolean existsByUserIdAndActiveTrue(Integer userId);
}
