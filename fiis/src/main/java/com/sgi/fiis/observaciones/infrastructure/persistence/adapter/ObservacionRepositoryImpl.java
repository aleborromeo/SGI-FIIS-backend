package com.sgi.fiis.observaciones.infrastructure.persistence.adapter;

import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.port.ObservacionRepository;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.ObservacionJpaEntity;
import com.sgi.fiis.observaciones.infrastructure.persistence.repository.ObservacionJpaRepository;
import com.sgi.fiis.observaciones.infrastructure.persistence.mapper.ObservacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ObservacionRepositoryImpl implements ObservacionRepository {

    private final ObservacionJpaRepository jpaRepository;
    private final ObservacionMapper mapper;

    @Override
    public Observacion save(Observacion observacion) {
        ObservacionJpaEntity entity = mapper.toJpa(observacion);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Observacion> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Observacion> findByIdTramite(Integer idTramite) {
        return jpaRepository.findByIdTramiteOrderByFechaRegistroDesc(idTramite)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Observacion> findPendientesByIdTramite(Integer idTramite) {
        return jpaRepository.findByIdTramiteAndEstadoObservacion(idTramite, "PENDIENTE")
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
