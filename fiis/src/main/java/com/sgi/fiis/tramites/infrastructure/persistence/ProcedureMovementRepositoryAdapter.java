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
    public List<ProcedureMovement> findByProcedureId(Long procedureId) {
        return repository.findByProcedure_IdOrderByMovementAtAsc(procedureId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ProcedureMovement toDomain(ProcedureMovementEntity entity) {
        return ProcedureMovement.builder()
                .actionUserId(entity.getActionUser().getId())
                .action(entity.getAction())
                .previousStatus(ProcedureStatus.valueOf(entity.getPreviousState()))
                .newStatus(ProcedureStatus.valueOf(entity.getNewState()))
                .comment(entity.getComment())
                .movementAt(entity.getMovementAt())
                .build();
    }
}
