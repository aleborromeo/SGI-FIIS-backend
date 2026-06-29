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
    public List<ProcedureMovement> findByProcedureId(Long idTramite) {
        return repository.findByProcedureIdOrderByDateAsc(idTramite)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ProcedureMovement toDomain(ProcedureMovementEntity entity) {
        return ProcedureMovement.builder()
                .idUsuarioAccion(entity.getIdUsuarioAccion())
                .accion(entity.getAccion())
                .estadoAnterior(ProcedureStatus.valueOf(entity.getEstadoAnterior()))
                .estadoNuevo(ProcedureStatus.valueOf(entity.getEstadoNuevo()))
                .observacion(entity.getObservacion())
                .fechaMovimiento(entity.getFechaMovimiento())
                .build();
    }
}
