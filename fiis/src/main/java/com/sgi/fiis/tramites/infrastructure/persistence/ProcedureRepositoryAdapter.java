package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupJpaRepository;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProcedureRepositoryAdapter implements ProcedureRepositoryPort {

    private final SpringDataProcedureRepository procedureRepository;
    private final SpringDataProcedureMovementRepository movementRepository;
    private final SpringDataUserRepository userRepository;
    private final ResearchGroupJpaRepository groupRepository;
    private final ProjectJpaRepository projectRepository;

    @Override
    @Transactional
    public Procedure save(Procedure tramite) {
        ProcedureEntity saved = procedureRepository.save(toEntity(tramite));

        long existentes = movementRepository.countByProcedure_Id(Long.valueOf(saved.getId()));
        tramite.getMovements().stream()
                .skip(existentes)
                .forEach(mov -> movementRepository.save(toMovimientoEntity(mov, saved.getId())));

        return toDomain(saved, new ArrayList<>(tramite.getMovements()));
    }

    @Override
    public Optional<Procedure> findById(Long id) {
        return procedureRepository.findById(id.intValue())
                .map(entity -> toDomain(entity, loadMovements(Long.valueOf(entity.getId()))));
    }

    @Override
    public Optional<Procedure> findByCode(String codigoTramite) {
        return procedureRepository.findByCode(codigoTramite)
                .map(entity -> toDomain(entity, loadMovements(Long.valueOf(entity.getId()))));
    }

    @Override
    public List<Procedure> findAll() {
        return procedureRepository.findAll()
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    private List<ProcedureMovement> loadMovements(Long procedureId) {
        return movementRepository.findByProcedure_IdOrderByMovementAtAsc(procedureId)
                .stream()
                .map(this::toMovimientoDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Procedure> findByApplicantId(Long idSolicitante) {
        return procedureRepository.findByApplicant_Id(idSolicitante)
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public List<Procedure> findByStatus(ProcedureStatus estado) {
        return procedureRepository.findByStatus(estado.name())
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public boolean existsByCode(String codigoTramite) {
        return procedureRepository.existsByCode(codigoTramite);
    }

    private Procedure toDomain(ProcedureEntity entity, List<ProcedureMovement> movements) {
        return Procedure.builder()
                .id(entity.getId() != null ? entity.getId().longValue() : null)
                .code(entity.getCode())
                .procedureType(entity.getProcedureType() != null ? ProcedureType.fromDbValue(entity.getProcedureType()) : null)
                .applicantId(entity.getApplicant() != null ? entity.getApplicant().getId().longValue() : null)
                .groupId(entity.getGroup() != null ? entity.getGroup().getId().longValue() : null)
                .currentStatus(entity.getStatus() != null ? ProcedureStatus.valueOf(entity.getStatus()) : null)
                .currentReviewerRole(entity.getReviewerRole() != null ? RoleEnum.valueOf(entity.getReviewerRole()) : null)
                .currentObservation(null)
                .projectReferenceId(entity.getProjectReference() != null ? entity.getProjectReference().getId().longValue() : null)
                .thesisReferenceId(entity.getThesisReferenceId())
                .reportReferenceId(entity.getReportReferenceId())
                .sentAt(entity.getSentAt())
                .updatedAt(entity.getUpdatedAt())
                .movements(movements)
                .build();
    }

    private ProcedureEntity toEntity(Procedure tramite) {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setId(tramite.getId() != null ? tramite.getId().intValue() : null);
        entity.setCode(tramite.getCode());
        entity.setProcedureType(tramite.getProcedureType() != null ? tramite.getProcedureType().getDbValue() : null);

        if (tramite.getApplicantId() != null) {
            entity.setApplicant(userRepository.getReferenceById(tramite.getApplicantId()));
        }

        if (tramite.getGroupId() != null) {
            entity.setGroup(groupRepository.getReferenceById(tramite.getGroupId().intValue()));
        }

        entity.setStatus(tramite.getCurrentStatus() != null ? tramite.getCurrentStatus().name() : null);
        entity.setReviewerRole(tramite.getCurrentReviewerRole() != null ? tramite.getCurrentReviewerRole().name() : null);
        entity.setSentAt(tramite.getSentAt());
        entity.setUpdatedAt(tramite.getUpdatedAt());

        if (tramite.getProjectReferenceId() != null) {
            entity.setProjectReference(projectRepository.getReferenceById(tramite.getProjectReferenceId().intValue()));
        }

        entity.setThesisReferenceId(tramite.getThesisReferenceId());
        entity.setReportReferenceId(tramite.getReportReferenceId());

        return entity;
    }

    private ProcedureMovement toMovimientoDomain(ProcedureMovementEntity entity) {
        ProcedureStatus previousStatus = entity.getPreviousState() != null ? ProcedureStatus.valueOf(entity.getPreviousState()) : null;
        ProcedureStatus newStatus = entity.getNewState() != null ? ProcedureStatus.valueOf(entity.getNewState()) : null;

        return ProcedureMovement.builder()
                .actionUserId(entity.getActionUser() != null ? entity.getActionUser().getId().longValue() : null)
                .action(entity.getAction())
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .comment(entity.getComment())
                .movementAt(entity.getMovementAt())
                .build();
    }

    private ProcedureMovementEntity toMovimientoEntity(ProcedureMovement domain, Integer procedureId) {
        ProcedureMovementEntity entity = new ProcedureMovementEntity();

        ProcedureEntity proc = new ProcedureEntity();
        proc.setId(procedureId);
        entity.setProcedure(proc);

        if (domain.getActionUserId() != null) {
            UserEntity actionUser = new UserEntity();
            actionUser.setId(domain.getActionUserId());
            entity.setActionUser(actionUser);
        }

        entity.setAction(domain.getAction());
        entity.setPreviousState(domain.getPreviousStatus() != null ? domain.getPreviousStatus().name() : null);
        entity.setNewState(domain.getNewStatus() != null ? domain.getNewStatus().name() : null);
        entity.setComment(domain.getComment());
        entity.setMovementAt(domain.getMovementAt());

        return entity;
    }
}
