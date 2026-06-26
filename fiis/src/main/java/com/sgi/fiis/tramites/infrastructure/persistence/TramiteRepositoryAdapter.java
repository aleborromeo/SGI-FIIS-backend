package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.MovimientoTramite;
import com.sgi.fiis.tramites.domain.model.TipoTramite;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.tramites.domain.port.TramiteRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TramiteRepositoryAdapter implements TramiteRepositoryPort {

    private final SpringDataTramiteRepository tramiteRepository;
    private final SpringDataMovimientoTramiteRepository movimientoRepository;

    @Override
    public Tramite guardar(Tramite tramite) {
        TramiteEntity saved = tramiteRepository.save(toEntity(tramite));

        // Movimientos son append-only: persiste solo los que aún no están en DB
        long existentes = movimientoRepository.countByIdTramite(saved.getId());
        tramite.getMovimientos().stream()
                .skip(existentes)
                .forEach(mov -> movimientoRepository.save(toMovimientoEntity(mov, saved.getId())));

        return toDomain(saved, new ArrayList<>(tramite.getMovimientos()));
    }

    @Override
    public Optional<Tramite> buscarPorId(Long id) {
        return tramiteRepository.findById(id)
                .map(entity -> toDomain(entity, cargarMovimientos(entity.getId())));
    }

    @Override
    public Optional<Tramite> buscarPorCodigo(String codigoTramite) {
        return tramiteRepository.findByCodigoTramite(codigoTramite)
                .map(entity -> toDomain(entity, cargarMovimientos(entity.getId())));
    }

    private List<MovimientoTramite> cargarMovimientos(Long idTramite) {
        return movimientoRepository.findByIdTramiteOrderByFechaMovimientoAsc(idTramite)
                .stream()
                .map(this::toMovimientoDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Tramite> buscarPorIdSolicitante(Long idSolicitante) {
        return tramiteRepository.findByIdSolicitante(idSolicitante)
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public List<Tramite> buscarPorEstado(EstadoTramite estado) {
        return tramiteRepository.findByEstadoActual(estado.name())
                .stream()
                .map(entity -> toDomain(entity, new ArrayList<>()))
                .toList();
    }

    @Override
    public boolean existePorCodigo(String codigoTramite) {
        return tramiteRepository.existsByCodigoTramite(codigoTramite);
    }

    private Tramite toDomain(TramiteEntity entity, List<MovimientoTramite> movimientos) {
        return Tramite.builder()
                .id(entity.getId())
                .codigoTramite(entity.getCodigoTramite())
                .tipoTramite(TipoTramite.valueOf(entity.getTipoTramite()))
                .idSolicitante(entity.getIdSolicitante())
                .idGrupo(entity.getIdGrupo())
                .estadoActual(EstadoTramite.valueOf(entity.getEstadoActual()))
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

    private TramiteEntity toEntity(Tramite tramite) {
        TramiteEntity entity = new TramiteEntity();
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

    private MovimientoTramite toMovimientoDomain(MovimientoTramiteEntity entity) {
        return MovimientoTramite.builder()
                .idUsuarioAccion(entity.getIdUsuarioAccion())
                .accion(entity.getAccion())
                .estadoAnterior(EstadoTramite.valueOf(entity.getEstadoAnterior()))
                .estadoNuevo(EstadoTramite.valueOf(entity.getEstadoNuevo()))
                .observacion(entity.getObservacion())
                .fechaMovimiento(entity.getFechaMovimiento())
                .build();
    }

    private MovimientoTramiteEntity toMovimientoEntity(MovimientoTramite domain, Long idTramite) {
        MovimientoTramiteEntity entity = new MovimientoTramiteEntity();
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
