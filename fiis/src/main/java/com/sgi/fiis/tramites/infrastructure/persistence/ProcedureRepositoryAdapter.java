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
        return tramiteRepository.findById(id)
                .map(entity -> toDomain(entity, cargarMovimientos(entity.getId())));
    }

    @Override
    public Optional<Procedure> findByCode(String codigoTramite) {
        return tramiteRepository.findByCodigoTramite(codigoTramite)
                .map(entity -> toDomain(entity, cargarMovimientos(entity.getId())));
    }

    private List<ProcedureMovement> cargarMovimientos(Long idTramite) {
        return movimientoRepository.findByProcedureIdOrderByDateAsc(idTramite)
                .stream()
                .map(this::toMovimientoDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Procedure> findByApplicantId(Long idSolicitante) {
        return tramiteRepository.findByIdSolicitante(idSolicitante)
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public List<Procedure> findByStatus(ProcedureStatus estado) {
        return tramiteRepository.findByEstadoActual(estado.name())
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public boolean existsByCode(String codigoTramite) {
        return tramiteRepository.existsByCodigoTramite(codigoTramite);
    }

    private Procedure toDomain(ProcedureEntity entity, List<ProcedureMovement> movimientos) {
        return Procedure.builder()
                .id(entity.getId())
                .codigoTramite(entity.getCodigoTramite())
                .tipoTramite(ProcedureType.valueOf(entity.getTipoTramite()))
                .idSolicitante(entity.getIdSolicitante())
                .idGrupo(entity.getIdGrupo())
                .estadoActual(ProcedureStatus.valueOf(entity.getEstadoActual()))
                .rolRevisorActual(entity.getRolRevisorActual() != null
                        ? RoleEnum.valueOf(entity.getRolRevisorActual()) : null)
                .observacionActual(entity.getObservacionActual())
                .idReferenciaProyecto(entity.getIdReferenciaProyecto())
                .idReferenciaTesis(entity.getIdReferenciaTesis())
                .idReferenciaInforme(entity.getIdReferenciaInforme())
                .fechaEnvio(entity.getFechaEnvio())
                .fechaActualizacion(entity.getFechaActualizacion())
                .movimientos(movimientos)
                .build();
    }

    private ProcedureEntity toEntity(Procedure tramite) {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setId(tramite.getId());
        entity.setCodigoTramite(tramite.getCodigoTramite());
        entity.setTipoTramite(tramite.getTipoTramite().name());
        entity.setIdSolicitante(tramite.getIdSolicitante());
        entity.setIdGrupo(tramite.getIdGrupo());
        entity.setEstadoActual(tramite.getEstadoActual().name());
        entity.setRolRevisorActual(tramite.getRolRevisorActual() != null
                ? tramite.getRolRevisorActual().name() : null);
        entity.setObservacionActual(tramite.getObservacionActual());
        entity.setIdReferenciaProyecto(tramite.getIdReferenciaProyecto());
        entity.setIdReferenciaTesis(tramite.getIdReferenciaTesis());
        entity.setIdReferenciaInforme(tramite.getIdReferenciaInforme());
        entity.setFechaEnvio(tramite.getFechaEnvio());
        entity.setFechaActualizacion(tramite.getFechaActualizacion());
        return entity;
    }

    private ProcedureMovement toMovimientoDomain(ProcedureMovementEntity entity) {
        return ProcedureMovement.builder()
                .idUsuarioAccion(entity.getIdUsuarioAccion())
                .accion(entity.getAccion())
                .estadoAnterior(ProcedureStatus.valueOf(entity.getEstadoAnterior()))
                .estadoNuevo(ProcedureStatus.valueOf(entity.getEstadoNuevo()))
                .observacion(entity.getObservacion())
                .fechaMovimiento(entity.getFechaMovimiento())
                .build();
    }

    private ProcedureMovementEntity toMovimientoEntity(ProcedureMovement domain, Long idTramite) {
        ProcedureMovementEntity entity = new ProcedureMovementEntity();
        entity.setIdTramite(idTramite);
        entity.setIdUsuarioAccion(domain.getIdUsuarioAccion());
        entity.setAccion(domain.getAccion());
        entity.setEstadoAnterior(domain.getEstadoAnterior().name());
        entity.setEstadoNuevo(domain.getEstadoNuevo().name());
        entity.setObservacion(domain.getObservacion());
        entity.setFechaMovimiento(domain.getFechaMovimiento());
        return entity;
    }
}
