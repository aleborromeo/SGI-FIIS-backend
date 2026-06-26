package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.MovimientoTramite;
import com.sgi.fiis.tramites.domain.port.MovimientoTramiteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovimientoTramiteRepositoryAdapter implements MovimientoTramiteRepositoryPort {

    private final SpringDataMovimientoTramiteRepository repository;

    @Override
    public List<MovimientoTramite> buscarPorIdTramite(Long idTramite) {
        return repository.findByIdTramiteOrderByFechaMovimientoAsc(idTramite)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private MovimientoTramite toDomain(MovimientoTramiteEntity entity) {
        return MovimientoTramite.builder()
                .idUsuarioAccion(entity.getIdUsuarioAccion())
                .accion(entity.getAccion())
                .estadoAnterior(EstadoTramite.valueOf(entity.getEstadoAnterior()))
                .estadoNuevo(EstadoTramite.valueOf(entity.getEstadoNuevo()))
                .observacion(entity.getObservacion())
                .fechaMovimiento(entity.getFechaMovimiento())
                .build();
    }
}
