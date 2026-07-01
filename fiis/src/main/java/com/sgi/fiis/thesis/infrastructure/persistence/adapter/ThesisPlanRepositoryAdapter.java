package com.sgi.fiis.thesis.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.sgi.fiis.thesis.domain.ThesisPlanStatus;
import com.sgi.fiis.thesis.domain.ThesisPlan;
import com.sgi.fiis.thesis.domain.port.out.ThesisPlanRepositoryPort;
import com.sgi.fiis.thesis.infrastructure.persistence.mapper.ThesisPlanMapper;
import com.sgi.fiis.thesis.infrastructure.persistence.repository.ThesisPlanJpaRepository;

@Repository
public class ThesisPlanRepositoryAdapter implements ThesisPlanRepositoryPort {
    private final ThesisPlanJpaRepository repository;
    private final ThesisPlanMapper mapper;

    public ThesisPlanRepositoryAdapter(ThesisPlanJpaRepository repository, ThesisPlanMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    public ThesisPlan save(ThesisPlan thesisPlan) { return mapper.toDomain(repository.save(mapper.toEntity(thesisPlan))); }
    public Optional<ThesisPlan> findById(Integer idPlanTesis) { return repository.findById(idPlanTesis).map(mapper::toDomain); }
    public List<ThesisPlan> findByEstudiante(Long idEstudiante) { return repository.findByIdEstudiante(idEstudiante).stream().map(mapper::toDomain).toList(); }
    public List<ThesisPlan> findByGrupo(Integer idGrupo) { return repository.findByIdGrupo(idGrupo).stream().map(mapper::toDomain).toList(); }
    public List<ThesisPlan> findByEstado(ThesisPlanStatus estadoPlan) { return repository.findByEstadoPlan(estadoPlan).stream().map(mapper::toDomain).toList(); }
}
