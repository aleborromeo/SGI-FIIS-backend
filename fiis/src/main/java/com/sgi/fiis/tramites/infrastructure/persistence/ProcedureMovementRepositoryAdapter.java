package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.port.ProcedureMovementRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcedureMovementRepositoryAdapter implements ProcedureMovementRepositoryPort {

    private final SpringDataProcedureMovementRepository repository;

    public ProcedureMovementRepositoryAdapter(SpringDataProcedureMovementRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProcedureMovement> findByProcedureId(Long idTramite) {
        return repository.findByProcedureIdOrderByDateAsc(idTramite.intValue())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ProcedureMovement toDomain(ProcedureMovementEntity entity) {
        return ProcedureMovement.builder()
                .idUsuarioAccion(entity.getActionUser() != null ? entity.getActionUser().getId().longValue() : null)
                .accion(entity.getAction())
                .estadoAnterior(ProcedureStatus.valueOf(entity.getPreviousState()))
                .estadoNuevo(ProcedureStatus.valueOf(entity.getNewState()))
                .observacion(entity.getComment())
                .fechaMovimiento(entity.getMovementAt())
                .build();
    }
}
