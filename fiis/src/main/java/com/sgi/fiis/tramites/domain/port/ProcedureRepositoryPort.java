package com.sgi.fiis.tramites.domain.port;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.Procedure;

import java.util.List;
import java.util.Optional;

public interface ProcedureRepositoryPort {

    Procedure save(Procedure tramite);

    Optional<Procedure> findById(Long id);

    Optional<Procedure> findByCode(String codigoTramite);

    List<Procedure> findByApplicantId(Long idSolicitante);

    List<Procedure> findByStatus(ProcedureStatus estado);

    boolean existsByCode(String codigoTramite);
}
