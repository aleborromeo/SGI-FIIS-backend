package com.sgi.fiis.resoluciones.infrastructure.adapter;

import com.sgi.fiis.resoluciones.domain.model.Resolucion;
import com.sgi.fiis.resoluciones.domain.port.out.ResolucionRepositoryPort;
import com.sgi.fiis.resoluciones.infrastructure.entity.ResolucionEntity;
import com.sgi.fiis.resoluciones.infrastructure.mapper.ResolucionMapper;
import com.sgi.fiis.resoluciones.infrastructure.repository.ResolucionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ResolucionRepositoryAdapter implements ResolucionRepositoryPort {

    private final ResolucionJpaRepository repository;
    private final ResolucionMapper mapper;

    public ResolucionRepositoryAdapter(ResolucionJpaRepository repository, ResolucionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Resolucion guardar(Resolucion resolucion) {
        ResolucionEntity entity = mapper.toEntity(resolucion);
        ResolucionEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Resolucion> buscarPorId(Long idResolucion) {
        return repository.findById(idResolucion)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existePorNumero(String numeroResolucion) {
        return repository.existsByNumeroResolucion(numeroResolucion);
    }
}
