package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListProceduresUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;

    public ListProceduresUseCase(ProcedureRepositoryPort procedureRepositoryPort) {
        this.procedureRepositoryPort = procedureRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<ProcedureResponseDto> execute() {
        return procedureRepositoryPort.findAll().stream()
                .map(ProcedureMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProcedureResponseDto> execute(RoleEnum rolUsuario) {
        if (rolUsuario == null) {
            return procedureRepositoryPort.findAll().stream()
                    .map(ProcedureMapper::toResponse)
                    .toList();
        }

        return switch (rolUsuario) {
            case DECANO -> procedureRepositoryPort
                    .findByStatus(ProcedureStatus.PENDIENTE_DECANATO)
                    .stream()
                    .map(ProcedureMapper::toResponse)
                    .toList();
            case COORDINADOR_GRUPO -> procedureRepositoryPort
                    .findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                    .stream()
                    .map(ProcedureMapper::toResponse)
                    .toList();
            case DIRECTOR_INVESTIGACION -> procedureRepositoryPort
                    .findByStatus(ProcedureStatus.PENDIENTE_DIRECCION)
                    .stream()
                    .map(ProcedureMapper::toResponse)
                    .toList();
            default -> procedureRepositoryPort.findAll().stream()
                    .map(ProcedureMapper::toResponse)
                    .toList();
        };
    }
}
