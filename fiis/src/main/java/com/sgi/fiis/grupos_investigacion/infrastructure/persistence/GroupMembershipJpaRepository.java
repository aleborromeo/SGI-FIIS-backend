package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupMembershipJpaRepository extends JpaRepository<GroupMembershipEntity, Integer> {
    
    Optional<GroupMembershipEntity> findByUserIdAndGroupId(Long userId, Integer groupId);

    @Query("SELECT g FROM GroupMembershipEntity g WHERE g.userId = :userId AND g.active = true")
    Optional<GroupMembershipEntity> findByUserIdAndActiveTrue(@Param("userId") Long userId);
}
