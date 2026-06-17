package com.sgi.fiis.observaciones.infrastructure.persistence.mapper;

import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.model.ObservacionEstado;
import com.sgi.fiis.observaciones.domain.model.TipoObservacion;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.ObservacionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper bidireccional entre {@link Observacion} (dominio) y {@link ObservacionJpaEntity} (JPA).
 */
@Component
public class ObservacionMapper {

    public Observacion toDomain(ObservacionJpaEntity entity) {
        return Observacion.builder()
                .id(entity.getIdObservacion())
                .idTramite(entity.getIdTramite())
                .idRevisor(entity.getIdRevisor())
                .tipoObservacion(TipoObservacion.valueOf(entity.getTipoObservacion()))
                .descripcion(entity.getDescripcion())
                .estado(ObservacionEstado.valueOf(entity.getEstadoObservacion()))
                .rolRevisor(entity.getRolRevisor())
                .fechaRegistro(entity.getFechaRegistro())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public ObservacionJpaEntity toJpa(Observacion domain) {
        return ObservacionJpaEntity.builder()
                .idObservacion(domain.getId())
                .idTramite(domain.getIdTramite())
                .idRevisor(domain.getIdRevisor())
                .tipoObservacion(domain.getTipoObservacion().name())
                .descripcion(domain.getDescripcion())
                .estadoObservacion(domain.getEstado().name())
                .rolRevisor(domain.getRolRevisor())
                .fechaRegistro(domain.getFechaRegistro())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}
