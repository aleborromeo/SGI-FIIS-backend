package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupJpaRepository;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListProceduresUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;
    private final ResearchGroupJpaRepository groupRepository;

    public ListProceduresUseCase(ProcedureRepositoryPort procedureRepositoryPort,
                                  ResearchGroupJpaRepository groupRepository) {
        this.procedureRepositoryPort = procedureRepositoryPort;
        this.groupRepository = groupRepository;
    }

    public List<ProcedureResponseDto> execute() {
        return procedureRepositoryPort.findAll().stream()
                .map(ProcedureMapper::toResponse)
                .toList();
    }

    public List<ProcedureResponseDto> execute(RoleEnum rolUsuario) {
        return execute(rolUsuario, null);
    }

    public List<ProcedureResponseDto> execute(RoleEnum rolUsuario, Long userId) {
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
            case COORDINADOR_GRUPO -> {
                if (userId != null) {
                    ResearchGroupEntity group = groupRepository.findByCurrentCoordinatorId(userId.intValue());
                    if (group != null) {
                        yield procedureRepositoryPort
                                .findByStatusAndGroupId(ProcedureStatus.PENDIENTE_COORDINADOR, group.getId().longValue())
                                .stream()
                                .map(ProcedureMapper::toResponse)
                                .toList();
                    }
                }
                yield procedureRepositoryPort
                        .findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                        .stream()
                        .map(ProcedureMapper::toResponse)
                        .toList();
            }
            case DIRECTOR_INVESTIGACION -> procedureRepositoryPort
                    .findByStatus(ProcedureStatus.PENDIENTE_DIRECCION)
                    .stream()
                    .map(ProcedureMapper::toResponse)
                    .toList();
            case ESTUDIANTE -> {
                if (userId != null) {
                    yield procedureRepositoryPort
                            .findByApplicantId(userId)
                            .stream()
                            .map(ProcedureMapper::toResponse)
                            .toList();
                }
                yield List.of();
            }
            default -> procedureRepositoryPort.findAll().stream()
                    .map(ProcedureMapper::toResponse)
                    .toList();
        };
    }
}
