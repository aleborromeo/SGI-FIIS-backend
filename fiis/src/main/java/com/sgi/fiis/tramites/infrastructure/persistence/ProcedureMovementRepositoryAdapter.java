package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.port.ProcedureMovementRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProcedureMovementRepositoryAdapter implements ProcedureMovementRepositoryPort {

    private final SpringDataProcedureMovementRepository repository;

    @Override
    public List<ProcedureMovement> findByProcedureId(Long procedureId) {
        return repository.findByProcedure_IdOrderByMovementAtAsc(procedureId.intValue())
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
