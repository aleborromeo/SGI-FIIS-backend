package com.sgi.fiis.tramites.domain.port;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.users.domain.model.RoleEnum;

import java.util.List;
import java.util.Optional;

public interface ProcedureRepositoryPort {

    Procedure save(Procedure tramite);

    Optional<Procedure> findById(Long id);

    List<Procedure> findAll();

    Optional<Procedure> findByCode(String codigoTramite);

    List<Procedure> findByApplicantId(Long idSolicitante);

    List<Procedure> findByStatus(ProcedureStatus estado);

    List<Procedure> findByReviewerRole(RoleEnum rolRevisor);

    List<Procedure> findByStatusAndReviewerRole(ProcedureStatus estado, RoleEnum rolRevisor);

    List<Procedure> findByStatusAndGroupId(ProcedureStatus estado, Long groupId);

    boolean existsByCode(String codigoTramite);
}
