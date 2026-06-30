package com.sgi.fiis.grupos_investigacion.domain.port;

import com.sgi.fiis.grupos_investigacion.domain.model.Membership;

import java.util.List;
import java.util.Optional;

public interface MembershipRepositoryPort {
    Membership save(Membership membership);
    Optional<Membership> findById(Integer id);
    Optional<Membership> findActiveByUserInGroup(Integer userId, Integer groupId);
    List<Membership> findActiveByGroup(Integer groupId);
    boolean existsActiveByUser(Integer userId);
}
