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
import lombok.RequiredArgsConstructor;
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

    private Procedure toDomain(ProcedureEntity entity, List<ProcedureMovement> movimientos) {
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
                .thesisReferenceId(null)
                .reportReferenceId(null)
                .sentAt(entity.getSentAt())
                .updatedAt(entity.getUpdatedAt())
                .movements(movimientos)
                .id(entity.getId() != null ? entity.getId().longValue() : null)
                .codigoTramite(entity.getCode())
                .tipoTramite(entity.getProcedureType() != null ? ProcedureType.valueOf(entity.getProcedureType()) : null)
                .idSolicitante(entity.getApplicant() != null ? entity.getApplicant().getId().longValue() : null)
                .idGrupo(entity.getGroup() != null ? entity.getGroup().getId().longValue() : null)
                .estadoActual(entity.getStatus() != null ? ProcedureStatus.valueOf(entity.getStatus()) : null)
                .rolRevisorActual(entity.getReviewerRole() != null
                        ? RoleEnum.valueOf(entity.getReviewerRole()) : null)
                .observacionActual(null)
                .idReferenciaProyecto(entity.getProjectReference() != null ? entity.getProjectReference().getId().longValue() : null)
                .idReferenciaTesis(entity.getThesisReferenceId())
                .idReferenciaInforme(entity.getReportReferenceId())
                .fechaEnvio(entity.getSentAt())
                .fechaActualizacion(entity.getUpdatedAt())
                .movimientos(movimientos)
                .build();
    }

    private ProcedureEntity toEntity(Procedure tramite) {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setId(tramite.getId() != null ? tramite.getId().intValue() : null);
        entity.setCode(tramite.getCode());
        entity.setProcedureType(tramite.getProcedureType().getDbValue());
        entity.setApplicant(userRepository.getReferenceById(tramite.getApplicantId()));
        entity.setGroup(groupRepository.getReferenceById(tramite.getGroupId().intValue()));
        entity.setStatus(tramite.getCurrentStatus().name());
        entity.setReviewerRole(tramite.getCurrentReviewerRole() != null
                ? tramite.getCurrentReviewerRole().name()
                : null);
        entity.setSentAt(tramite.getSentAt());
        entity.setUpdatedAt(tramite.getUpdatedAt());
        if (tramite.getProjectReferenceId() != null) {
            entity.setProjectReference(projectRepository.getReferenceById(tramite.getProjectReferenceId().intValue()));
        }
        entity.setCode(tramite.getCodigoTramite());
        entity.setProcedureType(tramite.getTipoTramite().name());
        
        if (tramite.getIdSolicitante() != null) {
            com.sgi.fiis.users.infrastructure.persistence.UserEntity user = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
            user.setId(tramite.getIdSolicitante());
            entity.setApplicant(user);
        }
        
        if (tramite.getIdGrupo() != null) {
            com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity rg = new com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity();
            rg.setId(tramite.getIdGrupo().intValue());
            entity.setGroup(rg);
        }
        
        entity.setStatus(tramite.getEstadoActual() != null ? tramite.getEstadoActual().name() : null);
        entity.setReviewerRole(tramite.getRolRevisorActual() != null
                ? tramite.getRolRevisorActual().name() : null);
                
        if (tramite.getIdReferenciaProyecto() != null) {
            com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity pe = new com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity();
            pe.setId(tramite.getIdReferenciaProyecto().intValue());
                entity.setProjectReference(pe);
        }

        entity.setThesisReferenceId(tramite.getIdReferenciaTesis());
        entity.setReportReferenceId(tramite.getIdReferenciaInforme());

        entity.setSentAt(tramite.getFechaEnvio());
        entity.setUpdatedAt(tramite.getFechaActualizacion());
        return entity;
    }

    private ProcedureMovement toMovimientoDomain(ProcedureMovementEntity entity) {
        return ProcedureMovement.builder()
                .actionUserId(entity.getActionUser().getId())
                .action(entity.getAction())
                .previousStatus(ProcedureStatus.valueOf(entity.getPreviousState()))
                .newStatus(ProcedureStatus.valueOf(entity.getNewState()))
                .comment(entity.getComment())
                .movementAt(entity.getMovementAt())
                .idUsuarioAccion(entity.getActionUser() != null ? entity.getActionUser().getId().longValue() : null)
                .accion(entity.getAction())
                .estadoAnterior(entity.getPreviousState() != null ? ProcedureStatus.valueOf(entity.getPreviousState()) : null)
                .estadoNuevo(entity.getNewState() != null ? ProcedureStatus.valueOf(entity.getNewState()) : null)
                .observacion(entity.getComment())
                .fechaMovimiento(entity.getMovementAt())
                .idDocumentoAdjunto(entity.getDocumentAttachmentId())
                .build();
    }

    private ProcedureMovementEntity toMovimientoEntity(ProcedureMovement domain, Integer procedureId) {
        ProcedureMovementEntity entity = new ProcedureMovementEntity();
        entity.setProcedure(procedureRepository.getReferenceById(procedureId));
        entity.setActionUser(userRepository.getReferenceById(domain.getActionUserId()));
        entity.setAction(domain.getAction());
        entity.setPreviousState(domain.getPreviousStatus().name());
        entity.setNewState(domain.getNewStatus().name());
        entity.setComment(domain.getComment());
        entity.setMovementAt(domain.getMovementAt());
        ProcedureEntity proc = new ProcedureEntity();
        proc.setId(idTramite);
        entity.setProcedure(proc);
        
        if (domain.getIdUsuarioAccion() != null) {
            com.sgi.fiis.users.infrastructure.persistence.UserEntity actionUser = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
            actionUser.setId(domain.getIdUsuarioAccion());
            entity.setActionUser(actionUser);
        }
        
        entity.setAction(domain.getAccion());
        entity.setPreviousState(domain.getEstadoAnterior() != null ? domain.getEstadoAnterior().name() : null);
        entity.setNewState(domain.getEstadoNuevo() != null ? domain.getEstadoNuevo().name() : null);
        entity.setComment(domain.getObservacion());
        entity.setMovementAt(domain.getFechaMovimiento());
        entity.setDocumentAttachmentId(domain.getIdDocumentoAdjunto());
        return entity;
    }
}
