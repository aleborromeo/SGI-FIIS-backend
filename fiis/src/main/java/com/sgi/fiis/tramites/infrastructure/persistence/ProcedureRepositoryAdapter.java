package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProcedureRepositoryAdapter implements ProcedureRepositoryPort {

    private final SpringDataProcedureRepository tramiteRepository;
    private final SpringDataProcedureMovementRepository movimientoRepository;

    @Override
    public Procedure save(Procedure tramite) {
        ProcedureEntity saved = tramiteRepository.save(toEntity(tramite));

        // Movimientos son append-only: persiste solo los que aún no están en DB
        long existentes = movimientoRepository.countByProcedureId(saved.getId());
        tramite.getMovements().stream()
                .skip(existentes)
                .forEach(mov -> movimientoRepository.save(toMovimientoEntity(mov, saved.getId())));

        return toDomain(saved, new ArrayList<>(tramite.getMovements()));
    }

    @Override
    public Optional<Procedure> findById(Long id) {
        return tramiteRepository.findById(id.intValue())
                .map(entity -> toDomain(entity, cargarMovimientos(entity.getId())));
    }

    @Override
    public Optional<Procedure> findByCode(String codigoTramite) {
        return tramiteRepository.findByCode(codigoTramite)
                .map(entity -> toDomain(entity, cargarMovimientos(entity.getId())));
    }

    private List<ProcedureMovement> cargarMovimientos(Integer idTramite) {
        return movimientoRepository.findByProcedureIdOrderByDateAsc(idTramite)
                .stream()
                .map(this::toMovimientoDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Procedure> findByApplicantId(Long idSolicitante) {
        return tramiteRepository.findByApplicantId(idSolicitante)
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public List<Procedure> findByStatus(ProcedureStatus estado) {
        return tramiteRepository.findByStatus(estado.name())
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public boolean existsByCode(String codigoTramite) {
        return tramiteRepository.existsByCode(codigoTramite);
    }

    private Procedure toDomain(ProcedureEntity entity, List<ProcedureMovement> movimientos) {
        return Procedure.builder()
                .id(entity.getId() != null ? entity.getId().longValue() : null)
                .codigoTramite(entity.getCode())
                .tipoTramite(ProcedureType.valueOf(entity.getProcedureType()))
                .idSolicitante(entity.getApplicant() != null ? entity.getApplicant().getId().longValue() : null)
                .idGrupo(entity.getGroup() != null ? entity.getGroup().getId().longValue() : null)
                .estadoActual(ProcedureStatus.valueOf(entity.getStatus()))
                .rolRevisorActual(entity.getReviewerRole() != null
                        ? RoleEnum.valueOf(entity.getReviewerRole()) : null)
                .observacionActual(null)
                .idReferenciaProyecto(entity.getProjectReference() != null ? entity.getProjectReference().getId().longValue() : null)
                .idReferenciaTesis(null)
                .idReferenciaInforme(null)
                .fechaEnvio(entity.getSentAt())
                .fechaActualizacion(entity.getUpdatedAt())
                .movimientos(movimientos)
                .build();
    }

    private ProcedureEntity toEntity(Procedure tramite) {
        ProcedureEntity entity = new ProcedureEntity();
        if (tramite.getId() != null) {
            entity.setId(tramite.getId().intValue());
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
        
        entity.setStatus(tramite.getEstadoActual().name());
        entity.setReviewerRole(tramite.getRolRevisorActual() != null
                ? tramite.getRolRevisorActual().name() : null);
                
        if (tramite.getIdReferenciaProyecto() != null) {
            com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity pe = new com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity();
            pe.setId(tramite.getIdReferenciaProyecto().intValue());
            entity.setProjectReference(pe);
        }

        entity.setSentAt(tramite.getFechaEnvio());
        entity.setUpdatedAt(tramite.getFechaActualizacion());
        return entity;
    }

    private ProcedureMovement toMovimientoDomain(ProcedureMovementEntity entity) {
        return ProcedureMovement.builder()
                .idUsuarioAccion(entity.getActionUser() != null ? entity.getActionUser().getId().longValue() : null)
                .accion(entity.getAction())
                .estadoAnterior(ProcedureStatus.valueOf(entity.getPreviousState()))
                .estadoNuevo(ProcedureStatus.valueOf(entity.getNewState()))
                .observacion(entity.getComment())
                .fechaMovimiento(entity.getMovementAt())
                .build();
    }

    private ProcedureMovementEntity toMovimientoEntity(ProcedureMovement domain, Integer idTramite) {
        ProcedureMovementEntity entity = new ProcedureMovementEntity();
        ProcedureEntity proc = new ProcedureEntity();
        proc.setId(idTramite);
        entity.setProcedure(proc);
        
        if (domain.getIdUsuarioAccion() != null) {
            com.sgi.fiis.users.infrastructure.persistence.UserEntity actionUser = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
            actionUser.setId(domain.getIdUsuarioAccion());
            entity.setActionUser(actionUser);
        }
        
        entity.setAction(domain.getAccion());
        entity.setPreviousState(domain.getEstadoAnterior().name());
        entity.setNewState(domain.getEstadoNuevo().name());
        entity.setComment(domain.getObservacion());
        entity.setMovementAt(domain.getFechaMovimiento());
        return entity;
    }
}
