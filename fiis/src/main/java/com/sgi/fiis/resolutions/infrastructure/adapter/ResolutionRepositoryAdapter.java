package com.sgi.fiis.resolutions.infrastructure.adapter;

import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.domain.port.out.ResolutionRepositoryPort;
import com.sgi.fiis.resolutions.infrastructure.entity.ResolutionEntity;
import com.sgi.fiis.resolutions.infrastructure.mapper.ResolutionMapper;
import com.sgi.fiis.resolutions.infrastructure.repository.ResolutionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ResolutionRepositoryAdapter implements ResolutionRepositoryPort {

    private final ResolutionJpaRepository repository;
    private final ResolutionMapper mapper;

    public ResolutionRepositoryAdapter(ResolutionJpaRepository repository, ResolutionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Resolution save(Resolution resolution) {
        ResolutionEntity entity = mapper.toEntity(resolution);
        ResolutionEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Resolution> findById(Long idResolucion) {
        return repository.findById(idResolucion)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByNumber(String numeroResolucion) {
        return repository.existsByNumeroResolucion(numeroResolucion);
    }
}
