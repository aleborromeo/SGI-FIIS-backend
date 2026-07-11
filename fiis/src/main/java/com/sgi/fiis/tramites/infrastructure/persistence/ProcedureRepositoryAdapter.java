package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupJpaRepository;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProcedureRepositoryAdapter implements ProcedureRepositoryPort {

    private final SpringDataProcedureRepository procedureRepository;
    private final SpringDataProcedureMovementRepository movementRepository;
    private final SpringDataUserRepository userRepository;
    private final ResearchGroupJpaRepository groupRepository;
    private final ProjectJpaRepository projectRepository;

    public ProcedureRepositoryAdapter(
            SpringDataProcedureRepository procedureRepository,
            SpringDataProcedureMovementRepository movementRepository,
            SpringDataUserRepository userRepository,
            ResearchGroupJpaRepository groupRepository,
            ProjectJpaRepository projectRepository) {
        this.procedureRepository = procedureRepository;
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public Procedure save(Procedure procedure) {
        ProcedureEntity saved = procedureRepository.save(toEntity(procedure));

        long existingCount = movementRepository.countByProcedure_Id(Long.valueOf(saved.getId()));
        procedure.getMovements().stream()
                .skip(existingCount)
                .forEach(mov -> movementRepository.save(toMovementEntity(mov, saved.getId())));

        return toDomain(saved, new ArrayList<>(procedure.getMovements()));
    }

    @Override
    public Optional<Procedure> findById(Long id) {
        return procedureRepository.findById(id.intValue())
                .map(entity -> toDomain(entity, loadMovements(Long.valueOf(entity.getId()))));
    }

    @Override
    public Optional<Procedure> findByCode(String code) {
        return procedureRepository.findByCode(code)
                .map(entity -> toDomain(entity, loadMovements(Long.valueOf(entity.getId()))));
    }

    private List<ProcedureMovement> loadMovements(Long procedureId) {
        return movementRepository.findByProcedure_IdOrderByMovementAtAsc(procedureId)
                .stream()
                .map(this::toMovementDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Procedure> findByApplicantId(Long applicantId) {
        return procedureRepository.findByApplicant_Id(applicantId)
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public List<Procedure> findByStatus(ProcedureStatus status) {
        return procedureRepository.findByStatus(status.name())
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return procedureRepository.existsByCode(code);
    }

    private Procedure toDomain(ProcedureEntity entity, List<ProcedureMovement> movements) {
        return Procedure.builder()
                .id(entity.getId().longValue())
                .code(entity.getCode())
                .procedureType(ProcedureType.fromDbValue(entity.getProcedureType()))
                .applicantId(entity.getApplicant().getId())
                .groupId(entity.getGroup().getId().longValue())
                .currentStatus(ProcedureStatus.valueOf(entity.getStatus()))
                .currentReviewerRole(entity.getReviewerRole() != null
                        ? RoleEnum.valueOf(entity.getReviewerRole())
                        : null)
                .currentObservation(null)
                .projectReferenceId(entity.getProjectReference() != null
                        ? entity.getProjectReference().getId().longValue()
                        : null)
                .thesisReferenceId(entity.getThesisReferenceId())
                .reportReferenceId(entity.getReportReferenceId())
                .sentAt(entity.getSentAt())
                .updatedAt(entity.getUpdatedAt())
                .movements(movements)
                .build();
    }

    private ProcedureEntity toEntity(Procedure procedure) {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setId(procedure.getId() != null ? procedure.getId().intValue() : null);
        entity.setCode(procedure.getCode());
        entity.setProcedureType(procedure.getProcedureType().getDbValue());
        entity.setApplicant(userRepository.getReferenceById(procedure.getApplicantId()));
        entity.setGroup(groupRepository.getReferenceById(procedure.getGroupId().intValue()));
        entity.setStatus(procedure.getCurrentStatus().name());
        entity.setReviewerRole(procedure.getCurrentReviewerRole() != null
                ? procedure.getCurrentReviewerRole().name()
                : null);
        entity.setSentAt(procedure.getSentAt());
        entity.setUpdatedAt(procedure.getUpdatedAt());
        if (procedure.getProjectReferenceId() != null) {
            entity.setProjectReference(projectRepository.getReferenceById(procedure.getProjectReferenceId().intValue()));
        }
        entity.setThesisReferenceId(procedure.getThesisReferenceId());
        entity.setReportReferenceId(procedure.getReportReferenceId());
        return entity;
    }

    private ProcedureMovement toMovementDomain(ProcedureMovementEntity entity) {
        return ProcedureMovement.builder()
                .actionUserId(entity.getActionUser().getId())
                .action(entity.getAction())
                .previousStatus(ProcedureStatus.valueOf(entity.getPreviousState()))
                .newStatus(ProcedureStatus.valueOf(entity.getNewState()))
                .comment(entity.getComment())
                .movementAt(entity.getMovementAt())
                .build();
    }

    private ProcedureMovementEntity toMovementEntity(ProcedureMovement domain, Integer procedureId) {
        ProcedureMovementEntity entity = new ProcedureMovementEntity();
        entity.setProcedure(procedureRepository.getReferenceById(procedureId));
        entity.setActionUser(userRepository.getReferenceById(domain.getActionUserId()));
        entity.setAction(domain.getAction());
        entity.setPreviousState(domain.getPreviousStatus().name());
        entity.setNewState(domain.getNewStatus().name());
        entity.setComment(domain.getComment());
        entity.setMovementAt(domain.getMovementAt());
        return entity;
    }
}
