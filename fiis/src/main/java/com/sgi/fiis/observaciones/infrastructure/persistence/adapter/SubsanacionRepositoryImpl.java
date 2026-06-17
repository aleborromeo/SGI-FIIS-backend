package com.sgi.fiis.observaciones.infrastructure.persistence.adapter;

import com.sgi.fiis.observaciones.domain.model.Subsanacion;
import com.sgi.fiis.observaciones.domain.port.SubsanacionRepository;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.SubsanacionJpaEntity;
import com.sgi.fiis.observaciones.infrastructure.persistence.repository.SubsanacionJpaRepository;
import com.sgi.fiis.observaciones.infrastructure.persistence.mapper.SubsanacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SubsanacionRepositoryImpl implements SubsanacionRepository {

    private final SubsanacionJpaRepository jpaRepository;
    private final SubsanacionMapper mapper;

    @Override
    public Subsanacion save(Subsanacion subsanacion) {
        SubsanacionJpaEntity entity = mapper.toJpa(subsanacion);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Subsanacion> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Subsanacion> findByIdObservacion(Integer idObservacion) {
        return jpaRepository.findByIdObservacionOrderByFechaRegistroAsc(idObservacion)
                .stream().map(mapper::toDomain).toList();
    }
}
