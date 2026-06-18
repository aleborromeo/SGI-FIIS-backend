package pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoPlanTesis;
import pe.unas.fiis.sgifiis.thesis.domain.PlanTesis;
import pe.unas.fiis.sgifiis.thesis.domain.port.out.PlanTesisRepositoryPort;
import pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.mapper.PlanTesisMapper;
import pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.repository.PlanTesisJpaRepository;

@Repository
public class PlanTesisRepositoryAdapter implements PlanTesisRepositoryPort {
    private final PlanTesisJpaRepository repository;
    private final PlanTesisMapper mapper;

    public PlanTesisRepositoryAdapter(PlanTesisJpaRepository repository, PlanTesisMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    public PlanTesis save(PlanTesis planTesis) { return mapper.toDomain(repository.save(mapper.toEntity(planTesis))); }
    public Optional<PlanTesis> findById(Integer idPlanTesis) { return repository.findById(idPlanTesis).map(mapper::toDomain); }
    public List<PlanTesis> findByEstudiante(Integer idEstudiante) { return repository.findByIdEstudiante(idEstudiante).stream().map(mapper::toDomain).toList(); }
    public List<PlanTesis> findByGrupo(Integer idGrupo) { return repository.findByIdGrupo(idGrupo).stream().map(mapper::toDomain).toList(); }
    public List<PlanTesis> findByEstado(EstadoPlanTesis estadoPlan) { return repository.findByEstadoPlan(estadoPlan).stream().map(mapper::toDomain).toList(); }
}
